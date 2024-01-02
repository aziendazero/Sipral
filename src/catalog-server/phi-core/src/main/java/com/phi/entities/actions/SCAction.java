package com.phi.entities.actions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.SC;

@BypassInterceptors
@Name("SCAction")
@Scope(ScopeType.CONVERSATION)
public class SCAction extends BaseAction<SC, Long> {

	private static final long serialVersionUID = 1651977666L;

	public static SCAction instance() {
		return (SCAction) Component.getInstance(SCAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Returns true if this list contains the specified element, false otherwise 
	 * @param cv -> the code whose presence in this list is to be tested
	 * @param scList -> the List<SC> target
	 * @return true if this list contains the specified CodeValue
	 */
	public boolean containCode(CodeValuePhi cv,List<SC> scList) {

		if(cv == null|| scList == null || scList.isEmpty()) {
			return false;
		}

		for(SC sc : scList) {
			if(cv.equals(sc.getCode())) { 
				return true;
			}
		}

		return false;
	}

	
	/**
	 * Inject in conversation a List<CodeValue> representing the children of a domain of the PHIDIC in the format domainName+"List"
	 * Example : injectSCListFromDictionary("Breathing")
	 * @param domainName -> the name of the domain
	 * @throws PersistenceException
	 * @throws DictionaryException
	 */
	public void injectSCListFromDictionary(String domainName) throws PersistenceException, DictionaryException {
		injectUpdatedSCListFromDictionary(domainName,null,null);
	}

	/**
	 *  Inject in conversation a List<CodeValue> representing the children of a domain of the PHIDIC in the format domainName+"List",
	 *  if the query have never been executed (listCodeDomain == null || listCodeDomain.isEmpty()). 
	 *  Moreover save in an HashTable if a listScBinding Code is contained in the listCodeDomain.
	 *  This information is used to check checkBox in the form f_need_nursing_assistance
	 * @param domainName -> the name of the domain
	 * @param listScBinding -> the SC List, normally taken from a binding
	 * @param listCodeDomain -> cached list
	 * @throws PersistenceException
	 * @throws DictionaryException
	 */
	public void injectUpdatedSCListFromDictionary(String domainName,List<SC> listScBinding, HashMap<String,List<CodeValue>> cacheCodeValueList) throws PersistenceException, DictionaryException {

		if(domainName == null || domainName.isEmpty()) {
			return;
		}
		
		List<CodeValue> listCodeDomain = new ArrayList<CodeValue>();
		
		if(cacheCodeValueList == null || cacheCodeValueList.isEmpty()) {
			cacheCodeValueList = new HashMap<String,List<CodeValue>>();
		}
		
		if(cacheCodeValueList.get(domainName+"CodeList") == null) { 
			//Vocabularies voc = VocabulariesImpl.instance();
			CodeValueAction cva = CodeValueAction.instance();
			CodeValue cvDomain = cva.getDomain("PHIDIC", domainName);
			listCodeDomain = (List<CodeValue>)cvDomain.getChildren(); 
		}
	
		else {
			listCodeDomain = cacheCodeValueList.get(domainName+"CodeList");
		}

		List<SC> listScFromDomain = new ArrayList<SC>(); 
		
		if(temporary == null) {
			temporary = new HashMap<String,Object>();
		}

		if(!listCodeDomain.isEmpty()) {

			HashMap<CodeValuePhi,Boolean> domainHashTable = new HashMap<CodeValuePhi,Boolean>();

			//if the binding Object.list is empty add to listScFromDomain all the elements from the specified domain as SC.code and update the hashTable.
			if(listScBinding == null) {
				for(CodeValue cv : listCodeDomain) {
					SC sc = new SC();
					sc.setCode((CodeValuePhi)cv);
					listScFromDomain.add(sc);
					domainHashTable.put((CodeValuePhi)cv, false);
				}

				temporary.put(domainName, domainHashTable);
				cacheCodeValueList.put(domainName+"CodeList", listCodeDomain);
				temporary.put("cacheCodeValueList", cacheCodeValueList);
				injectList(orderListBySequenceNumber(listScFromDomain), domainName+"List");

			}
			//otherwise copy the values from Object.list and add the listScFromDomain only the values which are still not present. 
			else {
				listScFromDomain.addAll(listScBinding);
				for(CodeValue cv : listCodeDomain) {
					SC sc = new SC();
					sc.setCode((CodeValuePhi)cv);
					//					listScFromDomain.add(sc);
					if(!containCode((CodeValuePhi)cv,listScBinding)){
						domainHashTable.put((CodeValuePhi)cv, false);
						listScFromDomain.add(sc);
					}
					else {
						domainHashTable.put((CodeValuePhi)cv, true);
					}
				}

				temporary.put(domainName, domainHashTable);
				cacheCodeValueList.put(domainName+"CodeList", listCodeDomain);
				temporary.put("cacheCodeValueList", cacheCodeValueList);
//				Contexts.getConversationContext().set(domainName+"CodeList", listCodeDomain);
				injectList(orderListBySequenceNumber(listScFromDomain), domainName+"List");
			}


		}


	}
	

	/**
	 * ComparatoByScore compare SC object by CodeValue SequenceNumber
	 * @author Vito
	 *
	 */
	private class ComparatorBySequenceNumber implements Comparator<SC>{

		@Override
		public int compare(SC sc1, SC sc2) {

			if (sc1 == null && sc2 == null) 
				return 0;

			if (sc1 == null)
				return -1;

			if (sc2 == null)
				return 1;
			
			if (sc1.getCode()== null && sc2.getCode() ==null)
				return 0;
			
			if (sc1.getCode() == null)
				return -1;
			
			if (sc2.getCode() == null)
				return 1;

			Integer sequenceNumber1 = (new Integer(sc1.getCode().getSequenceNumber()) == null ? null : new Integer(sc1.getCode().getSequenceNumber()));
			Integer sequenceNumber2 = (new Integer(sc2.getCode().getSequenceNumber()) == null ? null : new Integer(sc2.getCode().getSequenceNumber()));

			if (sequenceNumber1 == null && sequenceNumber2 == null)
				return 0;

			if (sequenceNumber1 == null)
				return -1;

			if (sequenceNumber2 == null)
				return 1;

			return (sequenceNumber1 - sequenceNumber2);
		}
	}

	/**
	 * Order SC List by Code Value sequence number
	 * @param listToOrder
	 * @return sorted list
	 */
	public List<SC> orderListBySequenceNumber(List<SC> listToOrder){

		if(listToOrder == null || listToOrder.isEmpty())
			return listToOrder;

		Collections.sort(listToOrder, new ComparatorBySequenceNumber());

		return listToOrder;


	}


}