package tech.muyi.util;

import com.fasterxml.jackson.databind.JavaType;
import com.google.gson.*;

import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:45
 */
public class JsonUtil {
    private static Gson gson = null;
    static {
        gson= new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    public static void toJson(Object src, Appendable writer) throws JsonIOException {
        gson.toJson(src, writer);
    }

    public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        gson.toJson(src, typeOfSrc, writer);
    }

    public static String toJson(JsonElement jsonElement) {
        return gson.toJson(jsonElement);
    }

    public static <T> T fromJson(String src, Type type) {
        return gson.fromJson(src, type);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT, JsonDeserializer<T> deserializer) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(classOfT, deserializer).create();
        return gson.fromJson(json, classOfT);
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        ArrayList<T> list = null;
        try {
            list = (ArrayList<T>) gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return list;
    }
}
