package tech.muyi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:45
 */
public class MyJson {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder().enableComplexMapKeySerialization()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .disableHtmlEscaping()
                .create();
    }

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> List<T> fromJsonToList(String jsonString, Class<T> elementType) {
        return GSON.fromJson(jsonString, TypeToken.getParameterized(List.class, elementType).getType());
    }
}
