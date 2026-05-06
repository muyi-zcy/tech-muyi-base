package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 消息队列异常
 *
 * <p>用于消息队列操作失败的场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class MessageQueueException extends SystemException {

    private String topic;
    private String operation;

    public MessageQueueException(String operation, String topic, Throwable cause) {
        super(CommonErrorCodeEnum.INTERNAL_SERVER_ERROR,
                String.format("消息队列操作失败: %s topic=%s", operation, topic),
                cause);
        this.operation = operation;
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public String getOperation() {
        return operation;
    }
}
