package com.phi.json;

import java.io.IOException;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.phi.entities.dataTypes.CodeValue;

/**
 * Serializes a HibernateProxy
 * Generated json will contain id and class name of the unproxied object
 * @author Alex Zupan
 */

public class HibernateProxySerializer extends JsonSerializer<HibernateProxy> {
	//extends com.fasterxml.jackson.datatype.hibernate3.HibernateProxySerializer {

//	public HibernateProxySerializer(boolean arg0) {
//		super(arg0);
//	}
	
	@Override
    public void serialize(HibernateProxy value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		
		LazyInitializer lazyInit = value.getHibernateLazyInitializer();

		jgen.writeStartObject();
		
		jgen.writeObjectField("entityName", lazyInit.getEntityName());
		
		if (value instanceof CodeValue) {
			jgen.writeObjectField("id", lazyInit.getIdentifier());
		} else {
			jgen.writeObjectField("internalId", lazyInit.getIdentifier());
		}
		
		jgen.writeEndObject();
		
    }
	
    public void serializeWithType(HibernateProxy value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
    	serialize(value, jgen, provider);
    }

}