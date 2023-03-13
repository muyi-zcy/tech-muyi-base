package tech.muyi.rocketmq;

import tech.muyi.core.MyTracer;
import tech.muyi.core.tracer.AbstractTracerClient;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class RocketMQConsumeTracerClient extends AbstractTracerClient {
    public RocketMQConsumeTracerClient(MyTracer myTracer) {
        super(myTracer);
    }
}
