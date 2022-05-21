package tech.muyi.mq;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.MDC;

import java.util.Iterator;
import java.util.List;

/**
 * @Author: muyi
 * @Date: 2021/1/31 3:57
 */
public class MyMqListener  implements MessageListenerConcurrently {
    private static Logger LOGGER = LoggerFactory.getLogger(MyMqListener.class);

    private MyMqMessageListener myMqMessageListener;

    public MyMqListener() {
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (null != list && !CollectionUtils.isEmpty(list)) {
            MyMqConsumeStatus myMqConsumeStatus = null;
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
                MessageExt messageExt = (MessageExt)iterator.next();
                String msgId = messageExt.getMsgId();
                if (null != msgId) {
                    MDC.put("traceId", msgId);
                }

                String topic = messageExt.getTopic();
                String tags = messageExt.getTags();
                int reconsumeTimes = messageExt.getReconsumeTimes();
                String message = null;

                try {
                    message = new String(Base64.decodeBase64(messageExt.getBody()), "UTF-8");
                    myMqConsumeStatus = this.myMqMessageListener.consumeMessage(message, reconsumeTimes);
                } catch (Throwable throwable) {
                    LOGGER.error("consume_message_exception,msgId:{},topic:{},tags:{},reconsumeTimes:{},smqConsumeStatus:{},message:{}", new Object[]{msgId, topic, tags, reconsumeTimes, myMqConsumeStatus, message, throwable});
                } finally {
                    LOGGER.debug("comsume_message,msgId:{},topic:{},tags:{},reconsumeTimes:{},smqConsumeStatus:{},message:{}", new Object[]{msgId, topic, tags, reconsumeTimes, myMqConsumeStatus, message});
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
