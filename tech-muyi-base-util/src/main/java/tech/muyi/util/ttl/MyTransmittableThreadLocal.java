package tech.muyi.util.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 带业务 code 的 TTL 包装。
 *
 * <p>与原生 {@link TransmittableThreadLocal} 的区别是额外携带了：
 * <ul>
 *   <li>code：上下文序列化后的键名</li>
 *   <li>classT：反序列化恢复时的目标类型</li>
 * </ul>
 */
@Slf4j
@Getter
public class MyTransmittableThreadLocal<T> extends TransmittableThreadLocal<T> {
    private String code;
    private Class classT;

    public MyTransmittableThreadLocal(String code, Class classT) {
        super();
        this.code = code;
        this.classT = classT;
        // 构造即注册，避免调用方遗漏注册步骤导致上下文无法上下行。
        MyTtlContextManager.register(this);
    }
}
