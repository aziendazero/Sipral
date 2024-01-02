package com.phi.entities.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;



@BypassInterceptors
@Name("ServiceDeliveryLocationAction")
@Scope(ScopeType.CONVERSATION)
public class ServiceDeliveryLocationAction extends BaseAction<ServiceDeliveryLocation, Long> {

	private static final long serialVersionUID = -5092382867532591845L;

	private static final Logger log = Logger.getLogger(ServiceDeliveryLocation.class);
	public static ServiceDeliveryLocationAction instance() {
		return (ServiceDeliveryLocationAction) Component.getInstance(ServiceDeliveryLocationAction.class, ScopeType.CONVERSATION);
	}
	/**
	 * set as faoriteIcd9Diag the codes of a list of Diagnonis passed.
	 * existing favoreDiagnonis, if any, are replaced.
	 * 
	 * @param list
	 * @throws PhiException
	 */
	/*@ShowInDesigner(description="Link the list of Diagnises ")
	public void addDiagnoses(List<Diagnosis> list) throws PhiException {

		List<CodeValueIcd9> listIcd9= new ArrayList<CodeValueIcd9>();
		if (list==null){
			return;
		}
		else if(list.isEmpty()){
			link ("favoriteICD9Diag",listIcd9);
		}
		
		for (Diagnosis diag : list) {
			CodeValueIcd9 diagIcd9 = diag.getCodeIcd9();
			listIcd9.add(diagIcd9);
		}

		List<CodeValueIcd9> favIcd9List = getEntity().getFavoriteICD9Diag();
		//remove all favoreIcd9List elements, if any.
		if(!favIcd9List.isEmpty()){
			unLink("favoriteICD9Diag", favIcd9List);
		}
		
		//link listIcd9 codes, coming from passed list.
		link("favoriteICD9Diag",listIcd9);
	
	}*/
	
	
	/**
	 * integrate the list of Diagnonis passed, with diagnosis with the code coming from
	 * favorite diagnonis of passed serviceDelivery Location. 
	 */
	
	/*public void linkDiagFromIcd9(List<Diagnosis> list, ServiceDeliveryLocation sdl){
		//ServiceDeliveryLocation sdl = this.getEntity();
		if(sdl.getFavoriteICD9Diag()== null){
			return;
		}
		else{
			List<CodeValueIcd9> favorIcd9 = sdl.getFavoriteICD9Diag();
			for(CodeValueIcd9 icd9Diag:favorIcd9){
				Diagnosis diag= new Diagnosis();
				diag.setCodeIcd9(icd9Diag);
				list.add(diag);
			}
			
		} 
	}*/


	public List<String> listscale (List<CodeValuePhi> list) {
		List<String> domList = new ArrayList<String>();
		if (list==null || list.size()==0 ) {
			domList.add("");
			return domList;
		}

		for (CodeValuePhi favorite : list) {
			domList.add(favorite.getDisplayName());
		}

		return domList;
	}

	public List<SelectItem> getAssessmentCodesForWard(){
		List<SelectItem> values = null;
		Vocabularies voc = VocabulariesImpl.instance();
		try {
			values = voc.getIdValues("PHIDIC:AssessmentScaleCode");
		} catch (Exception e) {
			log.error("Error loading AssessmentScale configuration for Wards", e);
		}
		if(Contexts.getApplicationContext().get("CUSTOMER").toString().equalsIgnoreCase("VCO")){
			if (values != null && !values.isEmpty()){
				SelectItem[] ciccio = values.toArray(new SelectItem[values.size()]);
				for (SelectItem item : ciccio) {
					if (item.getLabel().equalsIgnoreCase("NRS"))
						values.remove(item);				
				}
			}
		}
		return values;
	}
	
	/**
	 * From PHI CI
	 */

	public List<CodeValue> getNandaFavoritesOrdinate(List<CodeValue> favoriteNanda) {
		List<CodeValue> nandaFavoriteOrdinate = favoriteNanda; 
		if (favoriteNanda==null){
			return nandaFavoriteOrdinate;
		}
		Comparator<CodeValue> comparator = new Comparator<CodeValue>() {
			public int compare(CodeValue c1, CodeValue c2) {
				return c1.getDisplayName().compareTo(c2.getDisplayName());
			}
		};

		Collections.sort(nandaFavoriteOrdinate, comparator);

		return nandaFavoriteOrdinate;
	}

	public List<SelectItem>  getFavoriteList(List<CodeValue> listCv) {
		List<SelectItem> ret = new ArrayList<SelectItem>(); 

		for (CodeValue cv : listCv) {
			SelectItem selItem = new SelectItem(HibernateProxyHelper.getClassWithoutInitializingProxy(cv).getSimpleName() + "-" + cv.getId(), cv.getCode()+" - "+cv.getCurrentTranslation());
			ret.add(selItem);
		}

		return ret;
	}
	
	
	/**
	 * Find parent hospital of sdl
	 * @param sdl
	 * @return
	 */
	public ServiceDeliveryLocation getHospital(ServiceDeliveryLocation sdl) {
		ServiceDeliveryLocation sdlHospital = sdl;
		while (sdlHospital!=null && !"HOSP".equals(sdlHospital.getCode().getCode())) {
			sdlHospital = sdlHospital.getParent();
		}
		return sdlHospital;
	}
	
	/**
	 * Returns the list of area codes for UP and WARD, depending on customer customizations 
	 * @param customer
	 * @return
	 * @throws Exception
	 */
	public List<SelectItem> getSpecializationList(String customer) throws Exception{
		Vocabularies voc = VocabulariesImpl.instance();
		List<SelectItem> lst = voc.getIdValues("PHIDIC:Specialization");
		if(lst==null)
			lst = new ArrayList<SelectItem>();
		
		if("VCO".equals(customer)){
			List<SelectItem> addLst = voc.getIdValues("PHIDIC:SpecializationVCO");
			
			if(addLst!=null && !addLst.isEmpty()){
				lst.addAll(addLst);
			}
		}
		
		return lst;
	}
	
	private String selectedSDLColor=null;
	
	public String getSelectedSDLColor() {
		return selectedSDLColor;
	}

	public void setSelectedSDLColor(String selectedSDLColor) {
		this.selectedSDLColor = selectedSDLColor;
	}

	public String getHexColor() {
		getEntity();
		if (entity != null && entity.getColor() != null) {
			return Integer.toHexString(entity.getColor());
		}
		else 
			return "";
	}
	
	public void setColorAttribute() {
		
		  try {
			  
			  getEntity();
			  if (selectedSDLColor != null){
				  if (selectedSDLColor.indexOf("#")>=0){
					  selectedSDLColor = selectedSDLColor.substring(1);
				  }
				  int codiceColore = Integer.parseInt(selectedSDLColor, 16);
				  entity.setColor(codiceColore);
			  }			

		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		
	}
	
	public void injectSelected() {
		HashMap<String, Object> temp = getTemporary();

		if (!temp.isEmpty()) {
			ServiceDeliveryLocation sdl_id = (ServiceDeliveryLocation) temp.get("selectedSDL");
			inject(sdl_id);
		}
	}

	public ServiceDeliveryLocation getFromString(String value){
		ServiceDeliveryLocation sdloc = null;
		if (value != null && !value.isEmpty() && value.contains("-")) {

			String[] entityNameAndId = value.split("-");
			String entityName = entityNameAndId[0];
			if(!entityName.equals(ServiceDeliveryLocation.class.getName()))
				return null;
			
			Serializable id;
			if (entityNameAndId[1].matches("\\d*")) {
				id = Long.parseLong(entityNameAndId[1]);
			} else {
				id = entityNameAndId[1];
			}
			sdloc = ca.load(ServiceDeliveryLocation.class, id);
		}		
		
		return sdloc;
	}
}