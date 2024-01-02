package com.phi.cs.view.banner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.actions.ActionInterface;
import com.phi.entities.baseEntity.BaseEntity;

/**
 * Session bean for banner management.
 * Extended hashmap is used for derived values
 * 
 * Entities are object in session, refreshed at each conversation restart
 * 
 * @author alex.zupan
 */

@BypassInterceptors
@Name("Banner")
@Scope(ScopeType.SESSION)
public class Banner extends HashMap<String, Object> {

	private static final long serialVersionUID = 310928810887970872L;
	
	private static final Logger log = Logger.getLogger(Banner.class);

    public static Banner instance() {
        return (Banner) Component.getInstance(Banner.class, ScopeType.SESSION);
    }
	
	@Override
	public Object put(String key, Object value) {
		return super.put(key, value);
	}

	private HashMap<String, BaseEntity> entities = new HashMap<String, BaseEntity>();
	
	public BaseEntity getEntity(String conversationName) {
		return entities.get(conversationName);
	}
	
	public Collection<BaseEntity> getEntities() {
		return entities.values();
	}
	
	public Set<String> getEntitiesKeys() {
		return entities.keySet();
	}
	
	public void addEntity(String conversationName, BaseEntity entity) {
		if (entity == null) {
			return;
		}
		
		entities.put(conversationName, entity);
//		Contexts.getSessionContext().set(conversationName, entity);
	}
	
	public void removeEntity(String conversationName) {
		entities.remove(conversationName);
//		Contexts.getSessionContext().set(conver
	}

	public void refresh() {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
//		Context ctx = Contexts.getSessionContext();
		Context ctx = Contexts.getConversationContext();
		
		Set<String> keySet = new HashSet<String>(entities.keySet());
		Iterator<String> iterator = keySet.iterator();
		String conversationName = "";

		//for(String conversationName : entities.keySet()) {
		while(iterator.hasNext()) {
			conversationName = iterator.next(); 
			BaseEntity entity = null;
			try {
				entity = entities.get(conversationName);
				if (entity.getInternalId() > 0) {
					entity = (BaseEntity)ca.load(HibernateProxyHelper.getClassWithoutInitializingProxy(entity), entity.getInternalId());
				} else {
					log.error("[cid="+Conversation.instance().getId()+"] Skipping refresh of " + HibernateProxyHelper.getClassWithoutInitializingProxy(entity) + " internalId <= 0");
					entities.remove(conversationName);
				}
				
				if (entity instanceof BannerEntity) {
					ActionInterface<?> action = (ActionInterface<?>)Component.getInstance(conversationName+"Action");
					if (action instanceof BannerAction && ((BannerAction) action).remainInBanner(entity)) {
						entities.put(conversationName, entity);
						ctx.set(conversationName, entity);
					} else {
						entities.remove(conversationName);
					}
				} else {
					entities.put(conversationName, entity);
					ctx.set(conversationName, entity);
				}
			} catch (Exception e) {
				log.error("[cid="+Conversation.instance().getId()+"] Error refreshing object: " +HibernateProxyHelper.getClassWithoutInitializingProxy(entity) + " with id: " + entity.getInternalId() + " with conversation name: " + conversationName, e);
				entities.remove(conversationName);
			}
		}
	}

	/*protected boolean isEncounterStructureEnabled (PatientEncounter pe) {
		UserBean ub = UserBean.instance();
		if ((pe.getTemporarySDL()!= null && ub.getSdLocs().contains(pe.getTemporarySDL().getInternalId()))||ub.getSdLocs().contains(pe.getAssignedSDL().getInternalId())){
			return true;
		}
		return false;
	}*/
}