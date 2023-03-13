package tech.muyi.mq;

import lombok.extern.slf4j.Slf4j;
import mq.exception.MqErrorCodeEnum;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;
import tech.muyi.util.JsonUtil;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author: muyi
 * @Date: 2021/1/30 22:59
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({MyMqProductProperties.class})
public class MyMqProduct {
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private MyMqProductProperties myMqProductProperties;

    @PostConstruct
    public void init() {
        this.defaultMQProducer = new DefaultMQProducer(myMqProductProperties.getGroupName());
        this.defaultMQProducer.setNamesrvAddr(myMqProductProperties.getNamesrvAddr());
        this.defaultMQProducer.setInstanceName(this.getClass().getSimpleName());

        try {
            this.defaultMQProducer.start();
        } catch (MQClientException e) {
            throw new MyException(MqErrorCodeEnum.MQ_PRODUCT_EXCEPTION);
        }
    }

    /**
     * 序列化
     * @param topic
     * @param tag
     * @param key
     * @param body
     * @return
     */
    private Message convert(String topic, String tag, String key, Object body) {
        try {
            return new Message(topic, tag, key, Base64.encodeBase64(JsonUtil.toJson(body, body.getClass()).getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new MyException(CommonErrorCodeEnum.SERIALIZATION_FAIL, e);
        }
    }


    /**
     * 同步单条发送消息
     * @param topic
     * @param tag
     * @param key
     * @param body
     * @return
     */
    public SendResult send(String topic, String tag, String key, Object body) {
        Message message = null;
        try {
            message =  this.convert(topic, tag, key, body);
            return this.defaultMQProducer.send(message);
        } catch (MQBrokerException e){
            try {
                if (new Integer(2).equals(e.getResponseCode())) {
                    int tryTime = 5;
                    return this.retrySend(message, tryTime);
                } else {
                    throw e;
                }
            } catch (Exception e1) {
                throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e1);
            }
        } catch( MQClientException | RemotingException | InterruptedException e2) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e2);
        }
    }

    /**
     * 同步单条发送延时消息
     * @param topic
     * @param tag
     * @param key
     * @param body
     * @param timeout
     * @return
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    public SendResult send(String topic, String tag, String key, Object body,long timeout){
        Message message = null;
        try {
            message =  this.convert(topic, tag, key, body);
            return this.defaultMQProducer.send(message);
        } catch (MQBrokerException e){
            try {
                if ((new Integer(2)).equals(e.getResponseCode()) && message != null) {
                    int tryTime = 5;
                    return this.retrySend(message, tryTime,timeout );
                } else {
                    throw e;
                }
            } catch (Exception e1) {
                throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e1);
            }
        } catch( MQClientException | RemotingException | InterruptedException e2) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e2);
        }
    }


    /**
     * 批量同步发送
     * @param topic
     * @param tag
     * @param key
     * @param bodies
     * @return
     */
    public SendResult send(String topic, String tag, String key, List<?> bodies) {
        List<Message> messages = new ArrayList(bodies.size());
        bodies.forEach((body) -> {
            messages.add(this.convert(topic, tag, key, body));
        });
        try {
            return this.defaultMQProducer.send(messages);
        } catch (MQBrokerException e) {
            try {
                if ((new Integer(2)).equals(e.getResponseCode())) {
                    int tryTime = 5;
                    return this.retrySend(messages, tryTime);
                } else {
                    throw e;
                }
            } catch (Exception e1) {
                throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e1);
            }
        } catch (MQClientException | RemotingException | InterruptedException e2) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e2);
        }
    }

    /**
     * 批量同步延时发送
     * @param topic
     * @param tag
     * @param key
     * @param bodies
     * @param timeout
     * @return
     */
    public SendResult send(String topic, String tag, String key, List<?> bodies,long timeout) {
        List<Message> messages = new ArrayList(bodies.size());
        bodies.forEach((body) -> {
            messages.add(this.convert(topic, tag, key, body));
        });
        try {
            return this.defaultMQProducer.send(messages,timeout);
        } catch (MQBrokerException e) {
            try {
                if ((new Integer(2)).equals(e.getResponseCode())) {
                    int tryTime = 5;
                    return this.retrySend(messages, tryTime,timeout);
                } else {
                    throw e;
                }
            } catch (Exception e1) {
                throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e1);
            }
        } catch (MQClientException | RemotingException | InterruptedException e2) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION, e2);
        }
    }



    /**
     * 单条消息重试
     * @param message
     * @param retryTimes
     * @return
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    private SendResult retrySend(Message message, int retryTimes) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if (0 == retryTimes) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION);
        } else {
            Thread.sleep((new Random()).nextInt(10) + 1);
            try {
                return this.defaultMQProducer.send(message);
            } catch (MQBrokerException e) {
                --retryTimes;
                return this.retrySend(message, retryTimes);
            }
        }
    }
    private SendResult retrySend(Message message, int retryTimes,Long timeout) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if (0 == retryTimes) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION);
        } else {
            Thread.sleep((new Random()).nextInt(10) + 1);
            try {
                return this.defaultMQProducer.send(message,timeout);
            } catch (MQBrokerException e) {
                --retryTimes;
                return this.retrySend(message, retryTimes,timeout);
            }
        }
    }


    /**
     * 多条消息重试
     * @param messages
     * @param retryTimes
     * @return
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    private SendResult retrySend(List<Message> messages, int retryTimes) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if (0 == retryTimes) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION);
        } else {
            Thread.sleep((new Random()).nextInt(10) + 1);
            try {
                return this.defaultMQProducer.send(messages);
            } catch (MQBrokerException e) {
                -- retryTimes;
                return this.retrySend(messages, retryTimes);
            }
        }
    }
    private SendResult retrySend(List<Message> messages, int retryTimes,Long timeout) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if (0 == retryTimes) {
            throw new MyException(MqErrorCodeEnum.MQ_SYNC_SEND_FAIL_EXCEPTION);
        } else {
            Thread.sleep((new Random()).nextInt(10) + 1);
            try {
                return this.defaultMQProducer.send(messages,timeout);
            } catch (MQBrokerException e) {
                -- retryTimes;
                return this.retrySend(messages, retryTimes,timeout);
            }
        }
    }


    /**
     * 异步消息
     * @param topic
     * @param tag
     * @param key
     * @param body
     * @param sendCallback
     */
    public void asnySend(String topic, String tag, String key, Object body, SendCallback sendCallback){
        try {
            this.defaultMQProducer.send(this.convert(topic, tag, key, body),sendCallback);
        } catch (RemotingException | InterruptedException | MQClientException e) {
            throw new MyException(MqErrorCodeEnum.MQ_ASNY_SEND_FAIL_EXCEPTION, e);
        }
    }

    /**
     * 异步延时消息
     * @param topic
     * @param tag
     * @param key
     * @param body
     * @param timeout
     * @param sendCallback
     */
    public void asnySend(String topic, String tag, String key, Object body, long timeout , SendCallback sendCallback){
        try {
            this.defaultMQProducer.send(this.convert(topic, tag, key, body),sendCallback, timeout);
        } catch (RemotingException | InterruptedException | MQClientException e) {
            throw new MyException(MqErrorCodeEnum.MQ_ASNY_SEND_FAIL_EXCEPTION, e);
        }
    }

    /**
     * 单向消息
     * @param topic
     * @param tag
     * @param key
     * @param body
     */
    public void oneWaySend(String topic, String tag, String key, Object body){
        try {
            this.defaultMQProducer.sendOneway(this.convert(topic, tag, key, body));
        } catch (RemotingException | InterruptedException | MQClientException e) {
            throw new MyException(MqErrorCodeEnum.MQ_ONE_WAY_SEND_FAIL_EXCEPTION, e);
        }
    }
}
