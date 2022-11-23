package tech.muyi;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局上下文
 *
 * @author: muyi
 * @date: 2022/11/20
 **/
public class GlobalContext {
    private static TransmittableThreadLocal<Map<String, Object>> globalContext = new TransmittableThreadLocal<>();

    public static Map<String, Object> getGlobalContext() {
        if (globalContext == null) {
            return null;
        }
        return globalContext.get();
    }

    public static Map<String, Object> setGlobalContext(Map<String, Object> map) {
        if (globalContext == null) {
            globalContext = new TransmittableThreadLocal<>();
        }
        globalContext.set(map);
        return map;
    }

    public static void removeGlobalContext() {
        globalContext.remove();
    }

    public static Object setVariable(String key, Object variable) {
        Map<String, Object> map = getGlobalContext();
        if(map == null){
            map = new HashMap<>();
            setGlobalContext(map);
        }
        map.put(key, variable);
        return variable;
    }

    public static Object getVariable(String key){
        Map<String, Object> map = getGlobalContext();
        if(map == null){
            return null;
        }
        return map.get(key);
    }
}
