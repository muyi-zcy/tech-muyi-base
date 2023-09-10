package tech.muyi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:45
 */
public class MyJson {
    private static ObjectMapper objectMapper;

    public static void init(ObjectMapper objectMapper){
        MyJson.objectMapper = objectMapper;
    }

    private static ObjectMapper getObjectMapper(){
        if(objectMapper != null){
            return objectMapper;
        }
        try {
            objectMapper = ApplicationContextUtil.getBean(ObjectMapper.class);
        }catch (NullPointerException nullPointerException){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static String toJson(Object src) {
        try {
            return getObjectMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return getObjectMapper().readValue(json, classOfT);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
