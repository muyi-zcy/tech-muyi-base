package tech.muyi.util.ttl;

import org.apache.commons.lang3.StringUtils;
import tech.muyi.util.MyJson;

import java.util.HashMap;
import java.util.Map;

public class MyTtlContextManager {
    private static Map<String, MyTransmittableThreadLocal> transmittableThreadLocalMap = new HashMap<>();

    public static void register(MyTransmittableThreadLocal transmittableThreadLocal) {
        transmittableThreadLocalMap.put(transmittableThreadLocal.getCode(), transmittableThreadLocal);
    }

    public static Map<String, String> downAllData() {
        Map<String, String> data = new HashMap<>();
        for (String key : transmittableThreadLocalMap.keySet()) {
            Object object = transmittableThreadLocalMap.get(key).get();
            if (object != null) {
                data.put(key, MyJson.toJson(object));
            }
        }
        return data;
    }


    public static void upAllData(Map<String, String> data) {
        for (String key : data.keySet()) {
            if (transmittableThreadLocalMap.containsKey(key)) {
                MyTransmittableThreadLocal myTransmittableThreadLocal = transmittableThreadLocalMap.get(key);
                String value = data.get(key);
                if (StringUtils.isNotEmpty(value)) {
                    myTransmittableThreadLocal.set(MyJson.fromJson(value, myTransmittableThreadLocal.getClassT()));
                }
            }
        }
    }
}
