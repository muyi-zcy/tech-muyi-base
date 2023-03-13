package tech.muyi.rocketmq;

import tech.muyi.core.MyTracer;
import tech.muyi.core.tracer.AbstractTracerClient;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class RocketMQSendTracerClient extends AbstractTracerClient {

        public RocketMQSendTracerClient(MyTracer myTracer) {
        super(myTracer);
    }
}
