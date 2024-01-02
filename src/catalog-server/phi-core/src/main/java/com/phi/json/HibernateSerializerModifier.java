package com.phi.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.*;

public class HibernateSerializerModifier extends BeanSerializerModifier {
    protected final boolean _forceLoading;
    
    public HibernateSerializerModifier(boolean forceLoading) {
        _forceLoading = forceLoading;
    }

    @Override
    public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        return new PersistentCollectionSerializer(serializer);
    }

    @Override
    public JsonSerializer<?> modifyMapSerializer(SerializationConfig config, MapType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        return new PersistentCollectionSerializer(serializer);
    }
}