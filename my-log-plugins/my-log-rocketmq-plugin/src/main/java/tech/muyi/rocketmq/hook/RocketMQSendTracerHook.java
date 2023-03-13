package tech.muyi.rocketmq.hook;

import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class RocketMQSendTracerHook implements SendMessageHook {
    @Override
    public String hookName() {
        return RocketMQSendTracerHook.class.getCanonicalName();
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {

    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {

    }
}
