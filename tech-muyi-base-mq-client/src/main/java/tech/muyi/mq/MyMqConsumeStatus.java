package tech.muyi.mq;

/**
 * @Author: muyi
 * @Date: 2021/1/31 3:58
 */
public enum MyMqConsumeStatus {
    CONSUME_SUCCESS,
    RECONSUME_LATER;

    private MyMqConsumeStatus() {
    }
}
