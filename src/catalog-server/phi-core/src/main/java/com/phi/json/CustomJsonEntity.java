package com.phi.json;

import com.fasterxml.jackson.databind.JsonDeserializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entities annotated with this annotation will be deserialized with class returned by deserializer
 * witch must extend JsonDeserializer
 * Useful to set customDeserializer of *-backend
 *
 * Created by Alex on 28/07/2015.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomJsonEntity {
     Class<? extends JsonDeserializer> deserializer();
}