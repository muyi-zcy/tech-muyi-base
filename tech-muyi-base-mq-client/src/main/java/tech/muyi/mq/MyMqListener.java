package tech.muyi.mq;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: muyi
 * @Date: 2021/1/31 3:57
 */
@Slf4j
public class MyMqListener  implements MessageListenerConcurrently {
    private MyMqMessageListener myMqMessageListener;

    public MyMqListener() {
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (null != list && !CollectionUtils.isEmpty(list)) {
            MyMqConsumeStatus myMqConsumeStatus = null;

            for (MessageExt messageExt : list) {
                String msgId = messageExt.getMsgId();
                if (null != msgId) {
                    MDC.put("traceId", msgId);
                }

                String topic = messageExt.getTopic();
                String tags = messageExt.getTags();
                int reconsumeTimes = messageExt.getReconsumeTimes();
                String message = null;

                try {
                    message = new String(Base64.decodeBase64(messageExt.getBody()), StandardCharsets.UTF_8);
                    myMqConsumeStatus = this.myMqMessageListener.consumeMessage(message, reconsumeTimes);
                } catch (Throwable throwable) {
                    log.error("consume_message_exception,msgId:{},topic:{},tags:{},reconsumeTimes:{},smqConsumeStatus:{},message:{}", msgId, topic, tags, reconsumeTimes, myMqConsumeStatus, message, throwable);
                } finally {
                    log.debug("comsume_message,msgId:{},topic:{},tags:{},reconsumeTimes:{},smqConsumeStatus:{},message:{}", msgId, topic, tags, reconsumeTimes, myMqConsumeStatus, message);
                }
            }

            if (null == myMqConsumeStatus) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } else if (MyMqConsumeStatus.RECONSUME_LATER.name().equals(myMqConsumeStatus.name())) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } else {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        } else {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public MyMqMessageListener getMyMqMessageListener() {
        return myMqMessageListener;
    }

    public void setMyMqMessageListener(MyMqMessageListener myMqMessageListener) {
        this.myMqMessageListener = myMqMessageListener;
    }

}
