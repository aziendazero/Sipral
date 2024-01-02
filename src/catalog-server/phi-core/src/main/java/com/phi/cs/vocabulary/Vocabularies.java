package com.phi.cs.vocabulary;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import javax.ejb.Local;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.dataTypes.CodeValue;

@Local
public interface Vocabularies {

	public void cleanCaches();
	
	public List<SelectItem> getIdValues(String vocabName) throws Exception;
	
	public List<SelectItem> getIdValues(String vocabName, boolean retired) throws Exception;
	public List<SelectItem> getIdValues(String vocabName, boolean retired, boolean deleted) throws Exception;
	public List<SelectItem> getLazyIdValues(String vocabName) throws Exception;
	
		
	public List<SelectItem> getSpecializedIdValues(String vocabName) throws Exception;

	public List<SelectItem> getAbstractIdValues(String vocabName) throws Exception;

//	public abstract String getTranslation(CodeValue cv);
	
	public List<SelectItem> entityToSelectItem (Collection entities, String el4label) throws PhiException;
	public List<SelectItem> entityToSelectItem (Collection entities, String el4label, boolean alphaSort) throws PhiException;
	
	public List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues , boolean addCodeToLabel); 
	
	public  List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues);

	public  List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues, String defaultCode);
	
	public  List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues, String defaultCode, boolean addCodeToLabel );	
	
	public List<SelectItem> stringListToSelectItem ( List<String> strings);
	
	public boolean containsCode (Collection<CodeValue> codeValueList, String code);
	
//	public CodeValue getFromList (Collection<CodeValue> codeValueList, String code);
	
	public CodeValue getCodeValueOid(String oid) throws PersistenceException;
	
	public CodeValue getSpecializedCodeValue(String codeSystemName, String domainName, String code) throws PersistenceException, DictionaryException;
	
	public CodeValue getCodeValueCsDomainCode(String codeSystemName, String domainName, String code) throws PersistenceException, DictionaryException;
	public CodeValue getCodeValueCsDomainCodeDisableErrors(String codeSystemName, String domainName, String code) throws PersistenceException, DictionaryException;
	
	public CodeValue getCodeValue(String codeSystemName, String domainName, String code, String type) throws PersistenceException, DictionaryException;
	
	public CodeValue getCodeValue(String codeSystemName, String domainName, String code, String type, boolean disableErrors) throws PersistenceException, DictionaryException;

	
	public List<CodeValue> getCodeValues (String codeSystemName, String... domainNameCodes) throws PersistenceException, DictionaryException;
	public List<SelectItem> selectCodeValues (String codeSystemName, String... domainNameCodes) throws DictionaryException ;
	public List<SelectItem> selectCodeValuesWithDomains (String codeSystemName, String... domainNameCodes) throws DictionaryException;
	public List<SelectItem> distinctLabelSelectItem(List<SelectItem> sl) ;
	
//	public List<CodeValue> getTopLevelDomains (String codeSystemName) throws PersistenceException;
//	public CodeSystem getCodeSystem(String codeSystemName);
//	public Integer getCodeSystemId (String codeSystemName);
	
 	public String getCodeValueExtendedId(String codeSystemName, String... domainsAndCodes) throws PersistenceException, DictionaryException;
 	public CodeValue getDomain(String codeSystemName, String domainName) throws PersistenceException, DictionaryException;
 	//public String getCode(String cvid);

	public List<SelectItem> selectCodeValuesWithoutDomain (Boolean getLeavesOnly, String codeSystemName, String... codes) throws DictionaryException;
	
	public CodeValue getCodeValueByProperty(String codeSystemName,
			String domainName, String propertyName, String propertyValue)
			throws PersistenceException, DictionaryException;

}