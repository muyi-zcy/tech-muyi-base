package tech.muyi.rocketmq.hook;

import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class RocketMQConsumeTracerHook implements ConsumeMessageHook {
    @Override
    public String hookName() {
        return RocketMQConsumeTracerHook.class.getCanonicalName();
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {

    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {

    }
}
