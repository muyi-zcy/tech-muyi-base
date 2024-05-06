package tech.muyi.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MyObjectMapper {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }
        synchronized (MyObjectMapper.class) {
            try {
                objectMapper = ApplicationContextUtil.getBean(ObjectMapper.class);
            } catch (Throwable throwable) {
                objectMapper = new ObjectMapper();
            }
            return objectMapper;
        }
    }
}
