package com.phi.json;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.proxy.HibernateProxy;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
//import com.strategicgains.hyperexpress.domain.hal.HalResource;
//import com.strategicgains.hyperexpress.serialization.jackson.HalResourceSerializer;


public class HibernateSerializers extends Serializers.Base
{

//	public HibernateSerializers(boolean forceLoading) {
//		super(forceLoading);
//	}

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
    {
        Class<?> raw = type.getRawClass();
        
        if (HibernateProxy.class.isAssignableFrom(raw)) {
        	return new HibernateProxySerializer();
        } else if (Date.class.isAssignableFrom(raw) || Timestamp.class.isAssignableFrom(raw)) {
        	return new DateSerializer();
//        } else if (HalResource.class.isAssignableFrom(raw)) {
//        	return new HalResourceSerializer();
        }
        return null;
    }
}
