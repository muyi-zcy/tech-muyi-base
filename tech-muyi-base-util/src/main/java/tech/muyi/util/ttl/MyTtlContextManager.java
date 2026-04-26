package tech.muyi.util.ttl;

import org.apache.commons.lang3.StringUtils;
import tech.muyi.util.MyJson;

import java.util.HashMap;
import java.util.Map;

/**
 * TTL 上下文注册与批量上下行管理器。
 *
 * <p>约束：
 * <ul>
 *   <li>code 作为跨线程传递时的稳定 key，避免直接耦合变量名。</li>
 *   <li>上下文传输统一走 JSON 文本，降低线程池包装层对具体类型的依赖。</li>
 * </ul>
 *
 * <p>注意：该类当前使用普通 {@link HashMap} 保存注册表，建议在应用启动期完成注册；
 * 运行期动态频繁注册可能存在并发可见性风险。</p>
 */
public class MyTtlContextManager {
    private static Map<String, MyTransmittableThreadLocal> transmittableThreadLocalMap = new HashMap<>();

    public static void register(MyTransmittableThreadLocal transmittableThreadLocal) {
        // 后注册会覆盖同 code 的旧实例，确保 code 全局唯一。
        transmittableThreadLocalMap.put(transmittableThreadLocal.getCode(), transmittableThreadLocal);
    }

    public static Map<String, String> downAllData() {
        Map<String, String> data = new HashMap<>();
        for (String key : transmittableThreadLocalMap.keySet()) {
            Object object = transmittableThreadLocalMap.get(key).get();
            if (object != null) {
                // 只下传非空值，避免在子线程里无意义地覆盖默认上下文。
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
                    // 按注册时声明的 classT 反序列化，保证跨线程恢复类型一致。
                    myTransmittableThreadLocal.set(MyJson.fromJson(value, myTransmittableThreadLocal.getClassT()));
                }
            }
        }
    }
}
