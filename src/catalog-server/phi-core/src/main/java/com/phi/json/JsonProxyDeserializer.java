package com.phi.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonProxyDeserializer extends JsonDeserializer<JsonProxy> {

	@Override
	public JsonProxy deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {

		ObjectCodec oc = parser.getCodec();
        
        JsonNode node = oc.readTree(parser);
        
        String entityName = node.get("entityName").asText();
        long id = new Long(node.get("internalId").asText());
        
       	JsonProxy proxy = new JsonProxy(id, entityName);
       	
       	return proxy;
	}

}
