package com.phi.json;

import java.io.IOException;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.phi.entities.baseEntity.PrescriptionMedicineGeneric;

/**
 * PrescriptionMedicine and PrescriptionDischargeMedicine deserializer
 * Deserializes with default deserializer and after calls onPrePersist(); to populate dosageSerialized and templateDosageKey fields
 * @author Alex Zupan
 */

public class PrescriptionMedicineGenericDeserializer extends BeanDeserializerBase {

	private static final long serialVersionUID = -5306882735995445206L;
	
	private static final Logger log = Logger.getLogger(PrescriptionMedicineGenericDeserializer.class);
	
	private final JsonDeserializer<?> defaultDeserializer;

	public PrescriptionMedicineGenericDeserializer(BeanDeserializerBase defaultDeserializer) {
		super(defaultDeserializer);
		this.defaultDeserializer = defaultDeserializer;
	}
	
    @Override
    public PrescriptionMedicineGeneric deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

    	PrescriptionMedicineGeneric pm = (PrescriptionMedicineGeneric) defaultDeserializer.deserialize(jsonparser, deserializationcontext);

    	pm.onPrePersist();
    	
    	return pm;
    }

	@Override
	public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
		log.error("PrescriptionMedicineGenericDeserializer.unwrappingDeserializer TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
		log.error("PrescriptionMedicineGenericDeserializer.withObjectIdReader TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	public BeanDeserializerBase withIgnorableProperties(HashSet<String> ignorableProps) {
		log.error("PrescriptionMedicineGenericDeserializer.withIgnorableProperties TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	protected BeanDeserializerBase asArrayDeserializer() {
		log.error("PrescriptionMedicineGenericDeserializer.asArrayDeserializer TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	public Object deserializeFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.error("PrescriptionMedicineGenericDeserializer.deserializeFromObject TO BE IMPLEMENTED!!!");
		return null;
	}

	@Override
	protected Object _deserializeUsingPropertyBased(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.error("PrescriptionMedicineGenericDeserializer._deserializeUsingPropertyBased TO BE IMPLEMENTED!!!");
		return null;
	}
}