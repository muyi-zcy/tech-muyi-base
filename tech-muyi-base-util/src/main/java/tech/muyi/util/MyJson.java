package tech.muyi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:45
 */
public class MyJson {
    private static final Gson GSON;

    static {
        GSON = new GsonBuilder().enableComplexMapKeySerialization()
                .serializeNulls()
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

}
