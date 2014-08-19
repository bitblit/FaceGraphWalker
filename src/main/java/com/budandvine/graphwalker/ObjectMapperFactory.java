package com.budandvine.graphwalker;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;

/**
 * This is just an easy way to get a correctly configured ObjectMapper
 * cweiss : 6/24/12 6:25 PM
 */
public class ObjectMapperFactory implements FactoryBean<ObjectMapper> {

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public ObjectMapper getObject() throws BeansException {
        ObjectMapper rval = new ObjectMapper();
        rval.configure(SerializationFeature.INDENT_OUTPUT, true);
        rval.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        rval.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        return rval;
    }
}
