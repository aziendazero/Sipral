package com.phi.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.phi.entities.baseEntity.BaseEntity;
import org.apache.log4j.Logger;

/**
 * Selects the deserializer to use depending on beanDesc.getBeanClass()
 * @author Alex Zupan
 */

public class BeanDeserializerModifier extends com.fasterxml.jackson.databind.deser.BeanDeserializerModifier {

	private static final Logger log = Logger.getLogger(BeanDeserializerModifier.class);
	
	@Override 
	public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {

		//PrescriptionMedicine or PrescriptionDischargeMedicine must be deserialized with PrescriptionMedicineGenericDeserializer, 
		//witch deserializes the object and after calls onPrePersist(); to populate dosageSerialized and templateDosageKey fields

		if (beanDesc.getBeanClass().isAnnotationPresent(CustomJsonEntity.class)) {

			CustomJsonEntity annot = beanDesc.getBeanClass().getAnnotation(CustomJsonEntity.class);

			Class<? extends JsonDeserializer> customDeserializerClass = annot.deserializer();

			JsonDeserializer customDeserializer = null;

			try {
				Class<?>[] paramTypes = new Class<?>[1];
				paramTypes[0] = BeanDeserializerBase.class;

				customDeserializer = customDeserializerClass.getConstructor(paramTypes).newInstance(deserializer);
			} catch (Exception e) {
				log.error("Error finding custom deserializer for class: " + beanDesc.getBeanClass());
			}

			return customDeserializer;
		}

		//BaseEntities must be de serialized with BaseEntityDeserializer, witch discriminates if the object is a Proxy or not
		if (BaseEntity.getDerivedClasses().containsValue(beanDesc.getBeanClass())) {
			return new BaseEntityDeserializer((BeanDeserializer)deserializer);
		}
		return deserializer;
	}

}