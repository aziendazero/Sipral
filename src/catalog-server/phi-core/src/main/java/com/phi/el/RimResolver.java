package com.phi.el;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Target;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.IVL;

/**
 * ELResolver : handle creation of intermadiate objects during resolution of EL expressions
 * Example: #{Patient.person.name.fam} -> Creates person and name bean 
 * The object creation happens during the UPDATE MODEL VALUES and PROCESS VALIDATIONS phase of the JavaServer Faces lifecycle.
 * 
 * @author Alex Zupan
 */
public class RimResolver extends BeanELResolver implements PhaseListener {

	private static final long serialVersionUID = -3589233133555669193L;
	
	private static final Logger log = Logger.getLogger(RimResolver.class);
	
//	private boolean updating = false;
	
	public RimResolver() {
        // Acquire our Lifecycle instance
        try {
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);

            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
            
            lifecycle.addPhaseListener(this);
        } catch (FacesException e) {
        	throw new IllegalArgumentException("No Lifecycle!");
        }
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class getCommonPropertyType(ELContext context, Object base) {
		if (base != null && base instanceof BaseEntity) {
			Class clazz = super.getCommonPropertyType(context, base);
			return clazz;
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		if (base != null && base instanceof BaseEntity) {
			Iterator iterator = super.getFeatureDescriptors(context, base);
			return iterator;
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class getType(ELContext context, Object base, Object property) {
		if (base != null && base instanceof BaseEntity) {
			Class clazz = super.getType(context, base, property);
			return clazz;
		}
		return null;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {

		Boolean elUpdating = (Boolean)context.getContext(Boolean.class);
		boolean updating = false;
		if (elUpdating != null) {
			updating = elUpdating;
		} else {
			log.error("jboss-el.jar not patched!");
		}
		
		if (base == null) {

			if (BaseEntity.getDerivedClasses().containsKey(property)) {
				try {
					if (updating) {
						Context conCtx = Contexts.getConversationContext();

						if (conCtx == null) {
							return null;
							//throw new UnsupportedOperationException("No conversation running!");
						}
						
						Class<BaseEntity> clazz = BaseEntity.getDerivedClasses().get(property);
						Object object = clazz.newInstance();
						conCtx.set((String)property, object);

						context.setPropertyResolved(true);
						return object;
					}

				} catch (InstantiationException e) {
					throw new PropertyNotFoundException(e);
				} catch (IllegalAccessException e) {
					throw new PropertyNotFoundException(e);
				}
			}
		}
		
		if (base != null && (base instanceof BaseEntity || base.getClass().getName().equals("RTO") || base.getClass().getName().equals("IVL"))) {
			
			context.putContext(BaseEntity.class, base);
			
			Object object = super.getValue(context, base, property);
			
			if (updating && object == null) {
				
				Iterator<FeatureDescriptor> features = super.getFeatureDescriptors(context, base);
				
				while(features.hasNext()) {
					FeatureDescriptor feature = features.next();
					
					if (feature.getName().equals(property) && feature instanceof PropertyDescriptor) {
						
						PropertyDescriptor pd = (PropertyDescriptor)feature;
					try {
						Method wMethod = pd.getWriteMethod();
						Method rMethod = pd.getReadMethod();
//						Object obj = null;
						
						//Annotation @Target used in Act for EffectiveTime and ActivityTime and in ContextStructure for embeddable II
						Annotation annotation = rMethod.getAnnotation(Target.class);
						if (annotation != null) {
							Target trgt = (Target) annotation;
							if (trgt.value() != null) {
								object = trgt.value().newInstance();
							}
						}
			
						if (object == null) {
						
							Type[] typez = wMethod.getGenericParameterTypes();
							
							if (typez == null)
								return false;
							
							if (typez[0] instanceof ParameterizedType ) {
								ParameterizedType pt = (ParameterizedType)typez[0];
								Class clazz = (Class)pt.getRawType();
								if (clazz.isAssignableFrom(List.class) || clazz.isAssignableFrom(Set.class)) {
									Collection lstObj = (Collection)rMethod.invoke(base);
									
									if (lstObj == null) {
										if (clazz.isAssignableFrom(List.class)) {
											lstObj = new ArrayList();
										} else if (clazz.isAssignableFrom(Set.class)) {
											lstObj = new HashSet();	
										} else {
											throw new UnsupportedOperationException("Cannot create path " + property + " into " + base + " Path refers to a generic collection, cannot deduce collection type.");
										}

									}
									
									annotation = rMethod.getAnnotation(OneToMany.class);
									//Annotation @OneToMany with targetEntity used for II4Act, II4Role, II4Entity, II4ManagedParticipation, ED4Document, RTO4DoseCheck, RTO4MaxDose, IVL4ActivityTime, IVL4EffectiveTime
									if (annotation != null && ((OneToMany) annotation).targetEntity() != null) {
										OneToMany o2m = (OneToMany) annotation;

										lstObj.add(/*index, */o2m.targetEntity().newInstance());

									} else if (pt.getActualTypeArguments() != null) {
										ParameterizedType pt2 = (ParameterizedType)pt.getActualTypeArguments()[0];
										lstObj.add(/*index, */((Class)pt2.getRawType()).newInstance());
										
									} else
										throw new UnsupportedOperationException("Cannot create path " + property + " into " + base + " Path refers to a generic list, cannot deduce list type.");
									
									object = lstObj;
								} else {
									object = clazz.newInstance();
									
//									if (object instanceof RTO && pt.getActualTypeArguments() != null) {
//										RTO rto = (RTO)object;
//										Class ptNum = (Class)pt.getActualTypeArguments()[0];
//										Class ptDen = (Class)pt.getActualTypeArguments()[1];
//										rto.setNumerator(ptNum.newInstance());
//										rto.setDenominator(ptDen.newInstance());
//									}
									
									if (object instanceof IVL && pt.getActualTypeArguments() != null) {
										IVL ivl = (IVL)object;
										Class ptType = (Class)pt.getActualTypeArguments()[0];
										ivl.setLow(null);	//fix when instatiating new IVL<Date>, low and high values must be null
										ivl.setHigh(null);	//fix when instatiating new IVL<Date>, low and high values must be null
									}
									
								}
							} else if (typez[0] instanceof Class) {
								Class cl = (Class)typez[0];
								if (!cl.isAssignableFrom(Boolean.class)){
									annotation = rMethod.getAnnotation(ManyToOne.class);
									if (annotation != null && !((ManyToOne) annotation).targetEntity().equals(Void.TYPE)) {
										ManyToOne m2o = (ManyToOne) annotation;
										object = (m2o.targetEntity().newInstance());
									} else {
										/*
										 * Class.newInstance() can only invoke the zero-argument constructor and 
										 * Integer doesn't have ZERO argument constructor.
										 */
										if(!((Class)typez[0]).equals(Integer.class)){
											object = ((Class)typez[0]).newInstance();
										}
									}
								}
							}
						
						}
						
						wMethod.invoke(base, object);
						
						context.setPropertyResolved(true);
						
						break;
						
					} catch (Exception e) {
						throw new UnsupportedOperationException("Cannot create path " + property + " into " + base, e);
					}
					}
					
				}
			}
			
			return object;
		}
		
//		if (base != null && base instanceof PersistentSet) {
//			PersistentSet set = (PersistentSet)base;
//			set.getOwner()
//		}
		
		if (base != null && base instanceof Set) {
			Set set = (Set)base;
			Iterator ite = set.iterator();

			while(ite.hasNext()) {
				Object obj = ite.next();
				if (obj instanceof II) {
					II ii = (II)obj;
					if (ii.getRoot() == null) {
						ii.setRoot((String)property);
						context.setPropertyResolved(true);
						return ii;
					} else if (ii.getRoot().equals(property)) {
						context.setPropertyResolved(true);
						return ii;
					}
				}
			}
			if (updating) {
				try {
					BaseEntity parent = (BaseEntity)context.getContext(BaseEntity.class); 

					II ii = null;

					//Generic implementation:
					try {
						ii = (II)Class.forName("com.phi.entities.dataTypes.II4" +HibernateProxyHelper.getClassWithoutInitializingProxy(parent).getSimpleName()).newInstance();
					} catch (ClassNotFoundException e) {
						throw new UnsupportedOperationException("Cannot create path " + property + " into " + base + ". Cannot deduce parent type from: " + parent);
					}

					ii.setRoot((String)property);
					set.add(ii);
					
					context.setPropertyResolved(true);
					return ii;
				} catch (Exception e) {
					throw new UnsupportedOperationException("Cannot create path " + property + " into " + base, e);
				}
			} else {
				context.setPropertyResolved(true);
				return null;
			}
		}
		
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base != null && base instanceof BaseEntity) {
			boolean bool = super.isReadOnly(context, base, property);
			return bool;
		}
		return false;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {

		if (base != null) {
			if (base instanceof BaseEntity) {
			    if ("".equals(value)) {
			    	value = null;
			    }
				super.setValue(context, base, property, value);
			} else if (base instanceof BaseAction.FilterMap) {
				// if we are setting an xxxAction filter set null instead of empty string
				context.setPropertyResolved(true);
				if (value instanceof String && ((String) value).isEmpty() ||
						value instanceof String[] && ((String[])value).length == 0) {
					value=null;
				}
				try {
					((Map) base).put(property, value);
				} catch (UnsupportedOperationException e) {
					throw new PropertyNotWritableException(e);
				}
			}
		}
		
	}

	
	//Phase listener
	
	@Override
	public void afterPhase(PhaseEvent event) {
		if (event.getPhaseId().equals(PhaseId.UPDATE_MODEL_VALUES) || event.getPhaseId().equals(PhaseId.PROCESS_VALIDATIONS)) {
//			updating = false;
		}
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		if (event.getPhaseId().equals(PhaseId.UPDATE_MODEL_VALUES) || event.getPhaseId().equals(PhaseId.PROCESS_VALIDATIONS)) {
//			updating = true;
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}