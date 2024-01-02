package com.phi.json;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;

/**
 * Deserializer for CodeValues.
 * Two formats supported:
 * 1) entityName and id
 * 		Server will query for that entity with id
 * 2) codeSystemName, domainName and code. 
 * 		Server will search into db with: 	vocabularies.getCodeValueCsDomainCode(codeSystemName, domainName, code)
 * @author Alex Zupan
 * @param <T>
 */

public class CodeValueDeserializer<T extends CodeValue> extends JsonDeserializer<T> {

	protected static EntityManager em;
	private static final String ENTITY_NAME = "entityName";
	private static final String ID = "id";
	
	private static final String CODESYSTEM_NAME = "codeSystemName";
	private static final String DOMAIN_NAME = "domainName";
	private static final String CODE = "code";
	
	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {

        try {
			ObjectCodec oc = jp.getCodec();
			
			JsonNode node = oc.readTree(jp);
			
			//1) entityName and id
			JsonNode entityNameNode = node.get(ENTITY_NAME);
			JsonNode idNode = node.get(ID);

			if (entityNameNode != null && idNode != null) {
				if(em==null){
					return (T)CatalogPersistenceManagerImpl.instance().load(entityNameNode.asText(), idNode.asText());
				}
				else{
					return (T)em.getReference(Class.forName(entityNameNode.asText()),(Serializable) idNode.asText());
				}
					
			}
			
			//2) codeSystemName, domainName and code
			JsonNode codeSystemNameNode = node.get(CODESYSTEM_NAME);
			JsonNode domainNameNode = node.get(DOMAIN_NAME);
			JsonNode codeNode = node.get(CODE);
			
			if (codeSystemNameNode != null && domainNameNode != null && codeNode != null) {
				if(em==null){
					return (T)VocabulariesImpl.instance().getCodeValueCsDomainCode(codeSystemNameNode.asText(), domainNameNode.asText(), codeNode.asText());
				}
				else{
					VocabulariesImpl voc=new VocabulariesImpl(em);
					return (T)voc.getCodeValueCsDomainCode(codeSystemNameNode.asText(), domainNameNode.asText(), codeNode.asText());
				}
			}
			
			return null;
			
		} catch (Exception e) {
			throw new IOException("Error deserializing!", e);
		}
	}

	public static void setEm(EntityManager em) {
		CodeValueDeserializer.em = em;
	}
	
	

}