package com.stkj.supermarket.base.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;

public class JacksonUtils {

    public static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static String convertJsonString(Object obj) {
        String jsonString = "";
        try {
            jsonString = getDefaultObjectMapper().writeValueAsString(obj);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static <T> T convertJsonObject(String jsonString, Class<T> tClass) {
        if (jsonString == null) {
            return null;
        }

        T obj = null;
        try {
            obj = getDefaultObjectMapper().readValue(jsonString, tClass);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> List<T> convertJsonArray(String jsonString, Class<T> tClass) {
        if (jsonString == null) {
            return null;
        }

        List<T> tList = null;
        try {
            ObjectMapper objectMapper = getDefaultObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, tClass);
            tList = getDefaultObjectMapper().readValue(jsonString, javaType);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return tList;
    }

    public static HashMap<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        HashMap<String, Object> result = new HashMap<>();
        try {
            String jsonString = "";
            try {
                ObjectMapper defaultObjectMapper = getDefaultObjectMapper();
                jsonString = defaultObjectMapper.writeValueAsString(obj);
                result = defaultObjectMapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

}
