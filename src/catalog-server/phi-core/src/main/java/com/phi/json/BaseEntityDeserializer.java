package com.phi.json;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.entities.baseEntity.BaseEntity;

/**
 * Deserializer for BaseEntity
 * If json rapresentation contains entityName and internalId, load entity from db as proxy
 * else use default deserializer
 * @author Alex Zupan
 */

public class BaseEntityDeserializer extends BeanDeserializerBase {
	private static EntityManager em;
	private static final long serialVersionUID = -97195573211183345L;
	
	private static final Logger log = Logger.getLogger(BaseEntityDeserializer.class);
	
	private final JsonDeserializer<?> defaultDeserializer;
	
	public BaseEntityDeserializer(BeanDeserializerBase defaultDeserializer) {
		super(defaultDeserializer);
		this.defaultDeserializer = defaultDeserializer;
	}

	@Override
	public BaseEntity deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
		
        ObjectCodec oc = jsonparser.getCodec();
        
        JsonNode node = oc.readTree(jsonparser);
        
        JsonNode enNode = node.get("entityName");
        
        if (enNode != null) { //Load proxy
	        String entityName = enNode.asText();
	        long id = new Long(node.get("internalId").asText());
	        
			BaseEntity entity=null; 
			if(em==null){
				entity= (BaseEntity)CatalogPersistenceManagerImpl.instance().load(entityName, id);
			}
			else{
				try {
					entity= (BaseEntity)em.getReference(Class.forName(entityName),(Serializable) id);
				} catch (ClassNotFoundException e) {
					log.error("problem during test", e);
				}
			}
			return entity;
			
        } else { //Deserialize json
        	
        	JsonParser jpNew = oc.treeAsTokens(node);
        	jpNew.nextToken();
        	
        	BaseEntity entity = (BaseEntity)defaultDeserializer.deserialize(jpNew, deserializationcontext);

        	return entity;
        }
	}
	
	@Override
	public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
		log.error("BaseEntityDeserializer.unwrappingDeserializer TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
		log.error("BaseEntityDeserializer.withObjectIdReader TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	public BeanDeserializerBase withIgnorableProperties(HashSet<String> ignorableProps) {
		log.error("BaseEntityDeserializer.withIgnorableProperties TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	protected BeanDeserializerBase asArrayDeserializer() {
		log.error("BaseEntityDeserializer.asArrayDeserializer TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	public Object deserializeFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.error("BaseEntityDeserializer.deserializeFromObject TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	protected Object _deserializeUsingPropertyBased(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.error("BaseEntityDeserializer._deserializeUsingPropertyBased TO BE IMPLEMENTED!!!");
		return null;
	}
	
	public static void setEm(EntityManager em) {
		BaseEntityDeserializer.em = em;
	}
}