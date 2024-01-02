package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.banner.Banner;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Identification;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Contact;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("IdentificationAction")
@Scope(ScopeType.CONVERSATION)
public class IdentificationAction extends FilterForPrivacyAction<Identification, Long> {

	private static final long serialVersionUID = 520706624L;
	private static final Logger log = Logger.getLogger(IdentificationAction.class);
	
	private static final String QRY_PACEMAKER = "SELECT paceMaker FROM Identification id " +
			"WHERE id.patientEncounter.internalId = :patEncId";
	
	public static IdentificationAction instance() {
		return (IdentificationAction) Component.getInstance(
				IdentificationAction.class, ScopeType.CONVERSATION);
	}
	private static final String aidsForView= "AIDSFORVIEW";
	private static final String hearingAids = "HEARINGAIDS";
	private static final String walkingAids = "WALKINGAIDS";
	private static final String otherAids = "OTHERAIDS";
	private static final String valuesDelivered = "VALUESDELIVERED";


	public String showFormattedText(List<CodeValue> listCode){

		if(listCode == null || listCode.isEmpty()) 
			return "";
		
		int size=listCode.size();
		String text = "";

		for (int i=0;i<size;i++) {
			text = text + listCode.get(i).getCurrentTranslation();
			
			if (listCode.get(i).getCode().equals(aidsForView) && this.entity.getElseViewAids()!=null && !this.entity.getElseViewAids().equals("") )
				text = text + " [" + this.entity.getElseViewAids() + "]";
			
			else if (listCode.get(i).getCode().equals(hearingAids) && this.entity.getElseHearingAids()!=null && !this.entity.getElseHearingAids().equals("") ) 
				text = text + " [" + this.entity.getElseHearingAids() + "]";
			
			else if (listCode.get(i).getCode().equals(walkingAids) && this.entity.getElseWalkingAids()!=null && !this.entity.getElseWalkingAids().equals("") )
				text = text + " ["+this.entity.getElseWalkingAids() + "]";
			
			else if (listCode.get(i).getCode().equals(valuesDelivered) && this.entity.getElseWhat()!=null && !this.entity.getElseWhat().equals("") ) 
				text = text + " ["+this.entity.getElseWhat() + " a " +this.entity.getElseWhom() + "]";

			else if (listCode.get(i).getCode().equals(otherAids) && this.entity.getElseAids()!=null && !this.entity.getElseAids().equals("") )
				text = text + " [" + this.entity.getElseAids() + "]";
			
			if (i!=size-1) text= text + ", ";
		}

		return text;	

	}

	/**
	 * From PHI CI
	 */

	/**
	 * return a list of contact which are person of reference as well
	 * (used in MOD_Checking\CORE\FORMS\r_identification_filled.mmgp)
	 * @param listContact
	 * @return
	 * @throws PhiException 
	 */
	public List injectContactList() throws PhiException{
		
		
		
		IdataModel<Contact> listContact = ((IdataModel<Contact>)Contexts.getConversationContext().get("ContactList"));
		
		if(listContact == null || listContact.isEmpty()) {
			return listContact.getList();
		}
		
		List<Object> finalList = new ArrayList<Object>();
		for(Object contact : listContact.getList()) {
			if (contact instanceof Contact) {
				if(((Contact)contact).getPersonReference()) {
					finalList.add(contact);
				}
			} else if (contact instanceof Map) {
				if(Boolean.TRUE.equals(((Map)contact).get("personReference"))) {
					finalList.add(contact);
				}
			}
		}

		return finalList;
		
	}

	public String historyReportTitle(){
		String lang=Locale.getDefault().getLanguage();
		if (lang.equals("it"))
			return "STORICO INFORMAZIONI DI BASE";
		else if (lang.equals("de"))
			return "HISTOIRE BASISDATEN";
		else return "";	
	}

	public String reportTitle(){
		String lang=Locale.getDefault().getLanguage();
		if (lang.equals("it"))
			return "INFORMAZIONI DI BASE";
		else if (lang.equals("de"))
			return "BASISDATEN";
		else return "";	
	}

	public List<SelectItem> listLanguageFiltered() throws PhiException{

		List<SelectItem> languageList = new ArrayList<SelectItem>();
		ArrayList<CodeValue> tmpListCV = new ArrayList<CodeValue>(); 
		Vocabularies voc = VocabulariesImpl.instance();

		try{
			tmpListCV.add(voc.getCodeValueCsDomainCode("PHIDIC","Languages","it"));
			tmpListCV.add(voc.getCodeValueCsDomainCode("PHIDIC","Languages","de"));
			tmpListCV.add(voc.getCodeValueCsDomainCode("PHIDIC","Languages","ot"));

			languageList = voc.attributeToSelectItem(tmpListCV);

		}catch (Exception e) {
			// TODO: handle exception
			throw new PhiException(ErrorConstants.GENERIC_ERR_INTERNAL_MSG, e, ErrorConstants.GENERIC_ERR_INTERNAL_MSG);

		}
		return languageList;
	}

	/**
	 * Prints contact with reference 'yes' 
	 * (used in MOD_Checking\CORE\FORMS\f_basic_information_details.mmgp)
	 * 
	 * @param contactList
	 * @return
	 */
	@ShowInDesigner(description = "printReferencePersonList")
	public String printReferencePersonList(List<Contact> contactList){
		StringBuffer result=new StringBuffer("");
		String resultModified="";
		if(contactList !=null && !contactList.isEmpty()){
			try{
				for(Contact contact:contactList){
					if(contact!=null) {
						result.append(contact.getName().getGiv()).append(" ").append(contact.getName().getFam()).append(",");
					}
				}
			}catch(Exception e){
				log.error("Error to loading contact reference");
			}
		}
		resultModified=result.toString();
		if (!resultModified.isEmpty() && resultModified.endsWith(",")){
			resultModified=resultModified.substring(0, result.length() - 1);
		}
		return resultModified;
	}
	
	/**
	 * Links between Contact and Identification only the reference Person
	 * (used in MOD_Checking\CORE\PROCESSES\Identification.jpdl.xml)
	 * 
	 * @throws PhiException
	 */
	@ShowInDesigner(description = "linkContacts")
	public void linkContacts() throws PhiException{
		Context conversationContext = Contexts.getConversationContext();
		IdataModel<BaseEntity> resultsDm = (IdataModel<BaseEntity>)conversationContext.get("ContactList");
		Contact contact;
		if (resultsDm != null){
			for (BaseEntity roj: resultsDm.getList()){
				contact=(Contact) roj;				
				if (contact.getPersonReference()!=null){
					if (contact.getPersonReference()) {
						this.link("contact", contact);	
					} else {
						this.unLink("contact", contact);
					}
				}
			}
		}	
	}
	
	/**
	 * This method clean the field "OtherKindCode" when Identification.homeKindCode is empty
	 *  or "OtherProvenance" when Identification.otherProvenance is empty.
	 * Used in MOD_Checking\CORE\PROCESSES\Identification.jpdl.xml
	 * @param identification -> the entity in conversation to be updated
	 */
	@ShowInDesigner(description = "cleanOtherField")
	public void cleanOtherField(Identification identification) {
		
		if(identification == null) {
			return;
		}
		
		if(identification.getHomeKindCode() == null) {
			identification.setOtherHomeKind(null);
		}
		
		if(identification.getProvenanceCode() == null) {
			identification.setOtherProvenance(null);
		}
		
	}	
	
	@Observer(PatientEncounterAction.eventInjectIntoSession)
	public void patientEncounterInjected(Long patEncId) {
		String query = QRY_PACEMAKER;
		Query qry = ca.createQuery(query);
		qry.setParameter("patEncId", patEncId);
		List<Object[]> list = qry.getResultList();
		Banner banner = Banner.instance();
		if (list.size() == 1 && list.get(0)!= null) {
			banner.put("paceMaker",list.get(0));
		} else {
			banner.remove("paceMaker");
		}
	}
}