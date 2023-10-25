package tech.muyi.util.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MyTransmittableThreadLocal<T> extends TransmittableThreadLocal<T> {
    private String code;
    private Class classT;

    public MyTransmittableThreadLocal(String code, Class classT) {
        super();
        this.code = code;
        this.classT = classT;
        MyTtlContextManager.register(this);
    }
}
