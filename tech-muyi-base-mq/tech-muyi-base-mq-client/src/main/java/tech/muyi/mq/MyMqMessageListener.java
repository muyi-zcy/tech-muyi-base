package tech.muyi.mq;

/**
 * @Author: muyi
 * @Date: 2021/1/31 3:57
 */
public interface MyMqMessageListener {
    MyMqConsumeStatus consumeMessage(String message, Integer reconsumeTimes);
}