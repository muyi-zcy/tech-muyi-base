package tech.muyi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Gson 统一入口。
 *
 * <p>关键配置：
 * <ul>
 *   <li>long 按字符串输出，避免前端（特别是 JS）精度丢失。</li>
 *   <li>serializeNulls 保留空字段，减少跨服务协议歧义。</li>
 *   <li>disableHtmlEscaping 保持原文，避免日志/签名场景被转义影响。</li>
 * </ul>
 *
 * <p>该类使用单例 Gson，线程安全，可在多线程场景复用。</p>
 *
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
