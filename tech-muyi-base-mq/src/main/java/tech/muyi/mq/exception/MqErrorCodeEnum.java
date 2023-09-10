package tech.muyi.mq.exception;

import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;

/**
 * @author: muyi
 * @date: 2022/6/26
 **/

@ErrorCodeInfoAnno(name = "mq错误码",parentCode = "base-error-code", code = "mq-error-code", desc = "mq错误码-base")
public enum MqErrorCodeEnum implements BaseErrorInfoInterface {
    //    mq类型错误 7000
    MQ_PRODUCT_EXCEPTION("7101","MQ生产者启动异常"),
    MQ_CONSUMER_EXCEPTION("7102","MQ消费者启动异常"),
    MQ_ONE_WAY_SEND_FAIL_EXCEPTION("7103","MQ单向消息发送异常"),
    MQ_ASYNC_SEND_FAIL_EXCEPTION("7104","MQ异步消息发送异常"),
    MQ_SYNC_SEND_FAIL_EXCEPTION("7105","MQ同步消息发送异常"),
    MQ_DELAY_SEND_FAIL_EXCEPTION("7106","MQ延时消息发送异常")
    ;

    /** 错误码 */
    private String resultCode;

    /** 错误描述 */
    private String resultMsg;

    MqErrorCodeEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
