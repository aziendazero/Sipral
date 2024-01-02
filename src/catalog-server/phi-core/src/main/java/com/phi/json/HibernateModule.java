package com.phi.json;

import java.util.Date;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValueNanda;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.CodeValueStatus;

public class HibernateModule extends SimpleModule {

	private static final long serialVersionUID = -4078943158461705140L;

	public HibernateModule() {
		super("HibernateModule");
//        super("HibernateModule", new Version(1, 0, 0, "SNAPSHOT"));
    }
    
    //@SuppressWarnings("unchecked")
	@Override public void setupModule(SetupContext context) {
    	
        context.addSerializers(new HibernateSerializers());
        context.addBeanSerializerModifier(new HibernateSerializerModifier(false));
        
        //Added to deserialize proxies and dosages in a custom way:
        context.addBeanDeserializerModifier(new BeanDeserializerModifier());
        
//        SimpleSerializers serializers = new SimpleSerializers();
        SimpleDeserializers deserializers = new SimpleDeserializers();

//        serializers.addSerializer(MyEntity.class, new MyEntitySerializer());
        //deserializers.addDeserializer(HibernateProxy.class, new HibernateProxyDeserializer());

        deserializers.addDeserializer(CodeValue.class, new CodeValueDeserializer<CodeValue>());
        deserializers.addDeserializer(CodeValueCity.class, new CodeValueDeserializer<CodeValueCity>());
        //deserializers.addDeserializer(CodeValueCodifa.class, new CodeValueDeserializer<CodeValueCodifa>()); //Moved to medical-backend
        deserializers.addDeserializer(CodeValueCountry.class, new CodeValueDeserializer<CodeValueCountry>());
        //deserializers.addDeserializer(CodeValueDianoema.class, new CodeValueDeserializer<CodeValueDianoema>()); //Moved to medical-backend
        //deserializers.addDeserializer(CodeValueExemption.class, new CodeValueDeserializer<CodeValueExemption>()); //Moved to medical-backend
//        deserializers.addDeserializer(CodeValueIcd9.class, new CodeValueDeserializer<CodeValueIcd9>());//Moved to medical-backend
        deserializers.addDeserializer(CodeValueNanda.class, new CodeValueDeserializer<CodeValueNanda>());
        deserializers.addDeserializer(CodeValuePhi.class, new CodeValueDeserializer<CodeValuePhi>());
        deserializers.addDeserializer(CodeValueRole.class, new CodeValueDeserializer<CodeValueRole>());
        deserializers.addDeserializer(CodeValueStatus.class, new CodeValueDeserializer<CodeValueStatus>());

        deserializers.addDeserializer(Date.class, new DateDeserializer());
        
//        deserializers.addDeserializer(HalResource.class, new HalResourceDeserializer());
//        
//        context.addSerializers(serializers);
        context.addDeserializers(deserializers);
        
//        //HATEAOS
//        HalResourceFactory halFactory = new HalResourceFactory();
//        HyperExpress.registerResourceFactoryStrategy(halFactory, "application/hal+json");
//        HyperExpress.registerResourceFactoryStrategy(halFactory, "application/json");
//        
//        HyperExpress.relationships()
//
//     // Namespaces, CURIEs
//     .addNamespaces(
//         new Namespace("employee", "http://localhost:8080/spisal/resource/rest/employees/{rel}"),
//         new Namespace("serviceDeliveryLocation", "http://localhost:8080/spisal/resource/rest/servicedeliverylocations/{rel}")
//     )
//     
//     .forClass(Employee.class)
//         .rel(RelTypes.SELF, "/employee/{employeeId}")
//         .rel(RelTypes.UP, "/employee")
//
//     .forClass(ServiceDeliveryLocation.class)
//         .rel("responsible", "/employee/{employeeId}")
//         .rel(RelTypes.SELF, "/serviceDeliveryLocation/{serviceDeliveryLocationId}")
//         .rel(RelTypes.UP, "/serviceDeliveryLocation");
////         .rels("blog:children", "/blogs/{blogId}/entries/{entryId}")
    }
}