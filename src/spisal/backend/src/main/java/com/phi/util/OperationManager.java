package com.phi.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.ActionInterface;
import com.phi.entities.actions.CantiereAction;
import com.phi.entities.actions.DettagliBonificheAction;
import com.phi.entities.actions.EmployeeAction;
import com.phi.entities.actions.PersonAction;
import com.phi.entities.actions.PersoneGiuridicheAction;
import com.phi.entities.actions.PhysicianAction;
import com.phi.entities.actions.SediAction;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.InfortuniExt;
import com.phi.entities.baseEntity.PNCCantiere;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Vigilanza;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.phi.ps.ProcessManagerImpl;

@BypassInterceptors
@Name("OperationManager")
@Scope(ScopeType.EVENT)
public class OperationManager implements Serializable {

	private static final long serialVersionUID = 5112852801882150986L;

	public boolean hasDetail(Object root, String property){
		try {
			CodeValue cv = (CodeValue) MethodUtils.invokeExactMethod(root, "get"+property, ArrayUtils.EMPTY_OBJECT_ARRAY);
			if(cv!=null && cv.getCode()!=null){
				String code = cv.getCode();
				if("Ditta".equals(code)){
					code = "Sede";
				}
				if(MethodUtils.invokeExactMethod(root, "get"+property+code, ArrayUtils.EMPTY_OBJECT_ARRAY)!=null){
					return true;
				}
			}
		} catch (Exception e) {
			//nothing to do
		} 

		return false;
	}

	public void manageDetail(Object root, String property){
		try {
			CodeValue cv = (CodeValue) MethodUtils.invokeExactMethod(root, "get"+property, ArrayUtils.EMPTY_OBJECT_ARRAY);
			if(cv!=null && cv.getCode()!=null){
				String code = cv.getCode();
				if("Ditta".equals(code)){
					code = "Sede";
				}
				manageDetail(MethodUtils.invokeExactMethod(root, "get"+property+code, ArrayUtils.EMPTY_OBJECT_ARRAY));
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void manageDetail(Object obj) throws PhiException{

		Context conversationContext = Contexts.getConversationContext();
		OperationInfo info = new OperationInfo();
		info.setType("Details");
		conversationContext.set("operation",info);
		this.inject(obj);
	}

	//
	public void manageEdit(Object root, String property, String code, Object ...refresh){
		Context conversationContext = Contexts.getConversationContext();

		List<Object> refreshData = new ArrayList<Object>();
		if(refresh!=null && refresh.length>0){
			refreshData.addAll(Arrays.asList(refresh));
		}
		try{
			if(code!=null && !code.isEmpty()){
				refreshData.add(property+code);
				if("Ditta".equals(code)){
					code = "Sede";
				}

				property+=code;
			}

			refreshData.add(property);

			OperationInfo info = new OperationInfo();
			info.setType("Edit");
			info.setRefreshData(refreshData);
			info.setRoot(root);
			info.setProperty(property);
			info.setCode(code);

			conversationContext.set("operation",info);

			Object obj = MethodUtils.invokeExactMethod(root, "get"+property, ArrayUtils.EMPTY_OBJECT_ARRAY);
			this.inject(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	//
	public void manageLink(Object root, String property, String code, Object updateAddr, Boolean updateUbi, ActionInterface copyAteco, String additionalLink, String unlink) throws PhiException{
		Context conversationContext = Contexts.getConversationContext();
		OperationInfo info = new OperationInfo();
		Protocollo protocollo = (Protocollo) Component.getInstance("Protocollo");

		String getterMethod = "get"+property;
		if(code!=null && !code.isEmpty()){
			if("Ditta".equals(code)){
				code = "Sede";
			}
			getterMethod+=code;
		}

		Method getter = MethodUtils.getAccessibleMethod(root.getClass(), getterMethod, new Class[0]);
		if(getter!=null){
			info.setReturnType(getter.getReturnType());
		}	
		info.setRoot(root);
		info.setProperty(property);
		info.setCode(code);
		info.setAdditionalLink(additionalLink);
		info.setUnlink(unlink);
		info.setUpdateAddr(updateAddr);

		if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null && protocollo.getUos().getArea().getCode().equals("WORKMEDICINE"))
			info.setUpdateUbi(false);
		else 
			info.setUpdateUbi(updateUbi);

		info.setCopyAteco(copyAteco);
		info.setType("Link");

		conversationContext.set("operation", info);
		if(Person.class.equals(info.getReturnType())){
			//Utente
			ProcessManagerImpl.instance().manageTask("Utente;operation");

		}else if(Physician.class.equals(info.getReturnType())){
			//Medico
			ProcessManagerImpl.instance().manageTask("Medico;operation");

		}else if(Employee.class.equals(info.getReturnType())){
			//Interno
			ProcessManagerImpl.instance().manageTask("Interno;operation");

		}else if(Sedi.class.equals(info.getReturnType())){
			//Ditta
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(PersoneGiuridiche.class.equals(info.getReturnType())){
			//Ditta
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(Cantiere.class.equals(info.getReturnType())){
			//Cantiere
			ProcessManagerImpl.instance().manageTask("Cantiere;operation");

		}else{
			clearOperation();
		}
	}

	public void manageUnlink(Object root, String property, String code, Object updateAddr, Boolean updateUbi, ActionInterface copyAteco, String additionalLink, String unlink) throws PhiException{
		Context conversationContext = Contexts.getConversationContext();
		OperationInfo info = new OperationInfo();

		String getterMethod = "get"+property;
		if(code!=null && !code.isEmpty()){
			if("Ditta".equals(code)){
				code = "Sede";
			}
			getterMethod+=code;
		}

		Method getter = MethodUtils.getAccessibleMethod(root.getClass(), getterMethod, new Class[0]);
		if(getter!=null){
			info.setReturnType(getter.getReturnType());
		}	
		info.setRoot(root);
		info.setProperty(property);
		info.setCode(code);
		info.setAdditionalLink(additionalLink);
		info.setUnlink(unlink);
		info.setUpdateAddr(updateAddr);
		info.setUpdateUbi(updateUbi);		
		info.setCopyAteco(copyAteco);
		info.setType("Unlink");

		conversationContext.set("operation", info);
		if(Person.class.equals(info.getReturnType())){
			//Utente
			ProcessManagerImpl.instance().manageTask("Utente;operation");

		}else if(Physician.class.equals(info.getReturnType())){
			//Medico
			ProcessManagerImpl.instance().manageTask("Medico;operation");

		}else if(Employee.class.equals(info.getReturnType())){
			//Interno
			ProcessManagerImpl.instance().manageTask("Interno;operation");

		}else if(Sedi.class.equals(info.getReturnType())){
			//Ditta
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(PersoneGiuridiche.class.equals(info.getReturnType())){
			//Ditta
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(Cantiere.class.equals(info.getReturnType())){
			//Cantiere
			ProcessManagerImpl.instance().manageTask("Cantiere;operation");

		}else{
			clearOperation();
		}
	}

	public void performLink(){
		Context conversationContext = Contexts.getConversationContext();

		OperationInfo info = (OperationInfo) conversationContext.get("operation");
		if(info==null)
			return;

		Object root = info.getRoot();
		String code = info.getCode();
		String property = info.getProperty();
		Object updateAddr = info.getUpdateAddr();
		Boolean updateUbi = info.getUpdateUbi();


		ActionInterface<?> action = (ActionInterface<?>)Component.getInstance(info.getReturnType().getSimpleName()+"Action");
		Object target = action.getEntity();

		if(code!=null && !code.isEmpty()){
			List<String> unlinkList = new ArrayList<String>();
			unlinkList.add(property+"Utente");
			unlinkList.add(property+"Medico");
			unlinkList.add(property+"Interno");
			unlinkList.add(property+"Sede");
			unlinkList.add(property+"Ditta");
			unlinkList.add(property+"Cantiere");

			this.unLinkAll(root, unlinkList);

			Object target2 = null;
			if("Sede".equals(code)){
				//Ditta
				target2 = PersoneGiuridicheAction.instance().getEntity();

			}
			if(root instanceof Protocollo && "Ubicazione".equals(property) && "Cantiere".equals(code)){
				target2 = DettagliBonificheAction.instance().getEntity();
			}

			if(target!=null){
				try {
					MethodUtils.invokeMethod(root, "set"+property+code, new Object[]{target});

					if("Sede".equals(code) && target2!=null){
						MethodUtils.invokeMethod(root, "set"+property+"Ditta", new Object[]{target2});
						/*if(root instanceof Procpratiche && property.equalsIgnoreCase("riferimento")){
							((PersoneGiuridiche)target2).setNumPratiche(((PersoneGiuridiche)target2).getNumPratiche()+1);
							PersoneGiuridicheAction.instance().create((PersoneGiuridiche)target2);
							CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
							ca.flushSession();
						}*/

						//Medicina del Lavoro - Nel settare un richiedente di tipo utente, copiare lo stesso utente nei riferimenti 
					} else if(root instanceof Protocollo && "Richiedente".equals(property) && "Utente".equals(code)){
						Protocollo protocollo = (Protocollo) Component.getInstance("Protocollo");
						if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null && protocollo.getUos().getArea().getCode().equals("WORKMEDICINE")){
							Vocabularies vocabularies = VocabulariesImpl.instance();

							protocollo.setRiferimentoUtente((Person)target);
							protocollo.setRiferimento((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));
						}
					}
					// I0062163 
					else if(root instanceof Protocollo && "Ubicazione".equals(property) && "Cantiere".equals(code) && target instanceof Cantiere){
						Protocollo protocollo = (Protocollo) Component.getInstance("Protocollo");
						if("5".equals(protocollo.getCode().getCode())){
							DettagliBonifiche dettTarget2 = (DettagliBonifiche)target2;
							Vocabularies vocabularies = VocabulariesImpl.instance();
							Cantiere cant = (Cantiere)target;
							Committente comm = cant.getCommittente().get(0);

							if(comm.getPersoneGiuridiche()!=null){
								dettTarget2.setCommittenteBonifica((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
								dettTarget2.setCommittenteBonificaUtente(null);
								dettTarget2.setCommittenteBonificaDitta(comm.getPersoneGiuridiche());
							}else if(comm.getPerson()!=null){
								dettTarget2.setCommittenteBonifica((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));
								dettTarget2.setCommittenteBonificaDitta(null);
								dettTarget2.setCommittenteBonificaUtente(comm.getPerson());
							}
						}
					}


				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if("Sede".equals(code) && target2!=null){
				try {
					MethodUtils.invokeMethod(root, "set"+property+"Ditta", new Object[]{target2});
					/*if(root instanceof Procpratiche && property.equalsIgnoreCase("riferimento")){
						((PersoneGiuridiche)target2).setNumPratiche(((PersoneGiuridiche)target2).getNumPratiche()+1);
						PersoneGiuridicheAction.instance().create((PersoneGiuridiche)target2);
						CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
						ca.flushSession();
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{

			try {
				List<String> unlinkList = new ArrayList<String>();
				if(info.getUnlink()!=null && !info.getUnlink().isEmpty()){
					unlinkList.addAll(Arrays.asList(info.getUnlink().split(",")));
				}
				if(info.getAdditionalLink()!=null && !info.getAdditionalLink().isEmpty()){
					unlinkList.add(info.getAdditionalLink());
				}
				unlinkList.add(property);

				this.unLinkAll(root, unlinkList);
				if(target!=null){
					MethodUtils.invokeMethod(root, "set"+property, new Object[]{target});
				}
				if(info.getAdditionalLink()!=null && !info.getAdditionalLink().isEmpty()){
					String getterMethod = "get"+info.getAdditionalLink();
					Method getter = MethodUtils.getAccessibleMethod(root.getClass(), getterMethod, new Class[0]);
					Class<?> returnType = getter.getReturnType();
					ActionInterface<?> action2 = (ActionInterface<?>)Component.getInstance(returnType.getSimpleName()+"Action");
					Object target2 = action2.getEntity();
					if(target2!=null){
						MethodUtils.invokeMethod(root, "set"+info.getAdditionalLink(), new Object[]{target2});
					}

					if(info.getCopyAteco()!=null){
						if(target instanceof Sedi && target2 instanceof PersoneGiuridiche){
							MethodUtils.invokeMethod(info.getCopyAteco(), "copyAteco", new Object[]{target2, target});
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(updateAddr!=null){
			try {
				if(Boolean.TRUE.equals(updateAddr))
					updateAddr=root;
				String setterMethod = "setAddr";
				if("Ubicazione".equals(property)){
					setterMethod = "setUbicazioneAddr";
				}
				if(target!=null){
					AD addr = (AD) MethodUtils.invokeExactMethod(target, "getAddr", ArrayUtils.EMPTY_OBJECT_ARRAY);
					if(addr!=null){
						addr = addr.cloneAd();
					}
					MethodUtils.invokeMethod(updateAddr, setterMethod, new Object[]{addr});
				}

				// H0030665
				if(updateAddr instanceof InfortuniExt){
					String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
					String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
					if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setLatitudine", new Object[]{latitude});
					if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setLongitudine", new Object[]{longitude});
				}

				try{
					if(setterMethod.equals("setUbicazioneAddr")){
						if(updateAddr instanceof PraticheRiferimenti){	
							String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
							String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
							if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneX", new Object[]{latitude});
							if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneY", new Object[]{longitude});
						}else if(updateAddr instanceof Attivita){
							String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
							String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
							if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setLatitudine", new Object[]{latitude});
							if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setLongitudine", new Object[]{longitude});
						}else if(updateAddr instanceof Protocollo){
							String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
							String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
							if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneX", new Object[]{latitude});
							if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneY", new Object[]{longitude});
						}
					}

				}catch(Exception e){
					//unsupported reflection thing
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		if(Boolean.TRUE.equals(updateUbi)){
			info.setProperty("Ubicazione");
			info.setUpdateAddr(root);
			info.setUpdateUbi(false);
			try {
				Object type = MethodUtils.invokeExactMethod(root, "get"+property, ArrayUtils.EMPTY_OBJECT_ARRAY);
				MethodUtils.invokeMethod(root, "setUbicazione", new Object[]{type});
			} catch (Exception e) {

			} 

			performLink();
		}
	}

	public void performUnlink(){
		Context conversationContext = Contexts.getConversationContext();

		OperationInfo info = (OperationInfo) conversationContext.get("operation");
		if(info==null)
			return;

		Object root = info.getRoot();
		String code = info.getCode();
		String property = info.getProperty();
		Object updateAddr = info.getUpdateAddr();
		Boolean updateUbi = info.getUpdateUbi();

		if(code!=null && !code.isEmpty()){

			List<String> unlinkList = new ArrayList<String>();
			unlinkList.add(property+"Utente");
			unlinkList.add(property+"Medico");
			unlinkList.add(property+"Interno");
			unlinkList.add(property+"Sede");
			unlinkList.add(property+"Ditta");
			unlinkList.add(property+"Cantiere");			
			this.unLinkAll(root, unlinkList);

		}else{
			try {
				List<String> unlinkList = new ArrayList<String>();
				if(info.getUnlink()!=null && !info.getUnlink().isEmpty()){
					unlinkList.addAll(Arrays.asList(info.getUnlink().split(",")));
				}
				if(info.getAdditionalLink()!=null && !info.getAdditionalLink().isEmpty()){
					unlinkList.add(info.getAdditionalLink());
				}
				unlinkList.add(property);
				this.unLinkAll(root, unlinkList);

				if(info.getAdditionalLink()!=null && !info.getAdditionalLink().isEmpty()){
					if(info.getCopyAteco()!=null){
						MethodUtils.invokeMethod(info.getCopyAteco(), "copyAteco", new Object[]{null, null}, new Class[]{PersoneGiuridiche.class, Sedi.class});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(updateAddr!=null){
			try {
				if(Boolean.TRUE.equals(updateAddr))
					updateAddr=root;
				String setterMethod = "setAddr";
				if("Ubicazione".equals(property)){
					setterMethod = "setUbicazioneAddr";
				}
				MethodUtils.invokeMethod(updateAddr, setterMethod, new Object[]{null}, new Class[]{AD.class});


				// H0030665
				if(updateAddr instanceof InfortuniExt){
					setterMethod = "setLongitudine";
					MethodUtils.invokeMethod(updateAddr, setterMethod, new Object[]{null}, new Class[]{String.class});
					setterMethod = "setLatitudine";
					MethodUtils.invokeMethod(updateAddr, setterMethod, new Object[]{null}, new Class[]{String.class});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		if(Boolean.TRUE.equals(updateUbi)){
			info.setProperty("Ubicazione");
			info.setUpdateAddr(root);
			info.setUpdateUbi(false);

			performUnlink();
		}
	}

	public void clearOperation(){
		Context conversationContext = Contexts.getConversationContext();
		conversationContext.remove("operation");
	}

	public void refreshData(){
		Context conversationContext = Contexts.getConversationContext();
		OperationInfo info = (OperationInfo) conversationContext.get("operation");
		if(info==null)
			return;

		Object root = info.getRoot();
		List<Object> refresh = info.getRefreshData();

		try {
			if(refresh!=null && root!=null){
				for(Object property : refresh){
					Object obj = MethodUtils.invokeExactMethod(root, "get"+property, ArrayUtils.EMPTY_OBJECT_ARRAY);
					obj = refresh(obj);
					if(obj!=null && property!=null){
						MethodUtils.invokeMethod(root, "set"+property.toString(), new Object[]{obj});
						if(obj instanceof Sedi){
							PersoneGiuridiche p = ((Sedi)obj).getPersonaGiuridica();
							p.removeSedi((Sedi) obj);
							p.addSedi((Sedi) obj);
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void unLinkAll(Object root, List<String> unlinkList){

		if(unlinkList!=null && !unlinkList.isEmpty()){
			for(String unlink : unlinkList){
				try {


					/*if(unlink.equalsIgnoreCase("RiferimentoDitta") && root instanceof PraticheRiferimenti){
						PraticheRiferimenti r = (PraticheRiferimenti)root;
						if(r.getRiferimentoDitta()!=null){
							PersoneGiuridiche pg = r.getRiferimentoDitta();
							if(pg.getNumPratiche()>0){
								pg.setNumPratiche(pg.getNumPratiche()-1);
							}
							PersoneGiuridicheAction.instance().create(pg);
							CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
							ca.flushSession();

						}
					}*/

					Method getter = MethodUtils.getAccessibleMethod(root.getClass(), "get"+unlink, new Class[0]);
					Class<?> returnType = getter.getReturnType();
					MethodUtils.invokeMethod(root, "set"+unlink, new Object[]{null}, new Class[]{returnType});


				} catch (Exception e) {
					//do nothing
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object refresh(Object obj) throws PhiException{

		CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
		if(ca.contains(obj)){
			ca.refreshIfNeeded(obj);
		}else if(obj instanceof BaseEntity){
			try {
				Class currentClass = HibernateProxyHelper.getClassWithoutInitializingProxy(obj);
				ca.evict(obj);
				obj=ca.get(currentClass, ((BaseEntity) obj).getInternalId());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}else{
			return null;
		}

		return obj;
	}

	private void inject(Object obj) throws PhiException{
		if(obj instanceof Person){
			//Utente
			PersonAction.instance().inject(obj);
			ProcessManagerImpl.instance().manageTask("Utente;operation");

		}else if(obj instanceof Physician){
			//Medico
			PhysicianAction.instance().inject(obj);
			ProcessManagerImpl.instance().manageTask("Medico;operation");

		}else if(obj instanceof Employee){
			//Interno
			EmployeeAction.instance().inject(obj);
			ProcessManagerImpl.instance().manageTask("Interno;operation");

		}else if(obj instanceof Sedi){
			//Ditta
			PersoneGiuridicheAction.instance().inject(((Sedi) obj).getPersonaGiuridica());
			SediAction.instance().inject(obj);			
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(obj instanceof PersoneGiuridiche){
			//Ditta
			PersoneGiuridicheAction.instance().inject(obj);
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(obj instanceof Cantiere){
			//Cantiere
			CantiereAction.instance().inject(obj);
			ProcessManagerImpl.instance().manageTask("Cantiere;operation");

		}else{
			clearOperation();
		}
	}

	//
	public void manageUpdate(Object root, String property, String code, String id/*, Object updateAddr, Boolean updateUbi, ActionInterface copyAteco, String additionalLink, String unlink*/) throws PhiException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Context conversationContext = Contexts.getConversationContext();
		OperationInfo info = new OperationInfo();
		Protocollo protocollo = (Protocollo) Component.getInstance("Protocollo");

		String getterMethod = "get"+property;
		if(code!=null && !code.isEmpty()){
			if("Ditta".equals(code)){
				code = "Sede";
			}
			getterMethod+=code;
		}

		Method getter = MethodUtils.getAccessibleMethod(root.getClass(), getterMethod, new Class[0]);
		if(getter!=null){
			info.setReturnType(getter.getReturnType());
		}	
		info.setRoot(root);
		info.setProperty(property);
		info.setCode(code);

		/*
		info.setAdditionalLink(additionalLink);
		info.setUnlink(unlink);
		info.setUpdateAddr(updateAddr);

		if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null && protocollo.getUos().getArea().getCode().equals("WORKMEDICINE"))
			info.setUpdateUbi(false);
		else 
			info.setUpdateUbi(updateUbi);

		info.setCopyAteco(copyAteco);
		 */

		info.setType("Update");

		if(Person.class.equals(info.getReturnType())){
			//Utente
			ProcessManagerImpl.instance().manageTask("Utente;operation");

		}else if(Physician.class.equals(info.getReturnType())){
			//Medico
			ProcessManagerImpl.instance().manageTask("Medico;operation");

		}else if(Employee.class.equals(info.getReturnType())){
			//Interno
			ProcessManagerImpl.instance().manageTask("Interno;operation");

		}else if(Sedi.class.equals(info.getReturnType())){
			//Ditta
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(PersoneGiuridiche.class.equals(info.getReturnType())){
			//Ditta
			ProcessManagerImpl.instance().manageTask("Ditta;operation");

		}else if(Cantiere.class.equals(info.getReturnType())){
			//Cantiere
			//			Cantiere fromEntity = (Cantiere)getter.invoke(root, null);
			//			if (fromEntity instanceof HibernateProxy) {
			//				fromEntity = (Cantiere)((HibernateProxy)fromEntity).getHibernateLazyInitializer().getImplementation();
			//			}
			//			info.setCantiere(fromEntity);
			//info.setCode(id);
			conversationContext.set("operation", info);
			ProcessManagerImpl.instance().manageTask("Cantiere;operation");

		}else{
			clearOperation();
		}
	}

	public void performUpdate(){
		Context conversationContext = Contexts.getConversationContext();

		OperationInfo info = (OperationInfo) conversationContext.get("operation");
		if(info==null)
			return;

		Object root = info.getRoot();
		String code = info.getCode();
		String property = info.getProperty();
		Object updateAddr = info.getUpdateAddr();
		Boolean updateUbi = info.getUpdateUbi();


		ActionInterface<?> action = null;
		Object target = null;

		if(info.getReturnType() != null){
			action = (ActionInterface<?>)Component.getInstance(info.getReturnType().getSimpleName()+"LastAction");
			target = action.getEntity();
		}

		try {
			if(target!=null){
				if(root instanceof Infortuni || root instanceof Vigilanza){
					MethodUtils.invokeMethod(root, "set"+property, new Object[]{target});

				}else{
					MethodUtils.invokeMethod(root, "set"+property+info.getReturnType().getSimpleName(), new Object[]{target});
				}

				ActionInterface<?> rootAction = (ActionInterface<?>)Component.getInstance(root.getClass().getSimpleName()+"Action");
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

				if(root instanceof Vigilanza){
					ca.create(((Vigilanza)root).getProcpratiche());
				}else{
					ca.create(root);

				}
				//ca.flushSession();
			}else{
				if("Cantiere".equals(code) || "Cantiere".equals(property)){
					info.setReturnType(Cantiere.class);
				}

				if(info.getReturnType() != null){
					action = (ActionInterface<?>)Component.getInstance(info.getReturnType().getSimpleName()+"Action");
					target = action.getEntity();
				}

				if(Boolean.TRUE.equals(updateAddr))
					updateAddr=root;
				
				String setterMethod = "setAddr";
				String getterMethod = "getAddr";
				if(property!=null && property.contains("Ubicazione")){
					setterMethod = "setUbicazioneAddr";
					getterMethod = "getUbicazioneAddr";
				}

				if(target!=null && updateAddr!=null){
					AD addr = (AD) MethodUtils.invokeExactMethod(target, "getAddr", ArrayUtils.EMPTY_OBJECT_ARRAY);
					AD addr2 = (AD) MethodUtils.invokeExactMethod(updateAddr, getterMethod, ArrayUtils.EMPTY_OBJECT_ARRAY);

					if(addr!=null && !addr.equals(addr2)){
						addr = addr.cloneAd();
						MethodUtils.invokeMethod(updateAddr, setterMethod, new Object[]{addr});
					}

					if(updateAddr instanceof Attivita){
						String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String latitude2 = (String) MethodUtils.invokeExactMethod(updateAddr, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude2 = (String) MethodUtils.invokeExactMethod(updateAddr, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						if(!latitude.isEmpty() && !latitude.equals(latitude2)) MethodUtils.invokeMethod(root, "setLatitudine", new Object[]{latitude});
						if(!latitude.isEmpty() && !longitude.equals(longitude2)) MethodUtils.invokeMethod(root, "setLongitudine", new Object[]{longitude});
					}else if(updateAddr instanceof PraticheRiferimenti){	
						String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneX", new Object[]{latitude});
						if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneY", new Object[]{longitude});
					}else if(updateAddr instanceof Attivita){
						String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setLatitudine", new Object[]{latitude});
						if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setLongitudine", new Object[]{longitude});
					}else if(updateAddr instanceof Protocollo){
						String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneX", new Object[]{latitude});
						if(!latitude.isEmpty()) MethodUtils.invokeMethod(updateAddr, "setUbicazioneY", new Object[]{longitude});
					}else if(updateAddr instanceof InfortuniExt){
						String latitude = (String) MethodUtils.invokeExactMethod(target, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude = (String) MethodUtils.invokeExactMethod(target, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String latitude2 = (String) MethodUtils.invokeExactMethod(updateAddr, "getLatitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						String longitude2 = (String) MethodUtils.invokeExactMethod(updateAddr, "getLongitudine", ArrayUtils.EMPTY_OBJECT_ARRAY);
						if(!latitude.isEmpty() && !latitude.equals(latitude2)) MethodUtils.invokeMethod(updateAddr, "setLatitudine", new Object[]{latitude});
						if(!latitude.isEmpty() && !longitude.equals(longitude2)) MethodUtils.invokeMethod(updateAddr, "setLongitudine", new Object[]{longitude});
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateInfoForNewLink(Object newTarget){
		Context conversationContext = Contexts.getConversationContext();

		OperationInfo info = (OperationInfo) conversationContext.get("operation");
		if(info==null)
			return;

		if (newTarget instanceof HibernateProxy) {
			newTarget = (Object)((HibernateProxy)newTarget).getHibernateLazyInitializer().getImplementation();
		}

		if(!info.getProperty().equals(info.getCode())){
			info.setProperty(info.getProperty().replace(info.getCode(), ""));
		}
		
		Cantiere cantConversation = (Cantiere) conversationContext.get("Cantiere");
			info.setCantiere(cantConversation);

		info.setReturnType(newTarget.getClass());
		
		if(info.getRoot() instanceof Infortuni){
			info.setUpdateAddr(((Infortuni) info.getRoot()).getInfortuniExt());
		}else if(info.getRoot() instanceof Attivita){
			info.setUpdateAddr(info.getRoot());
		}else if (info.getRoot() instanceof PraticheRiferimenti && info.getProperty().contains("Ubicazione")){
			info.setUpdateAddr(true);
		}else if (info.getRoot() instanceof Protocollo && info.getProperty().contains("Ubicazione")){
			info.setUpdateAddr(true);
		}else{
			info.setUpdateAddr(null);
		}
	}
}
