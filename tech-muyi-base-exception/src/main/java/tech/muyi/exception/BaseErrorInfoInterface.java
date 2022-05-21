package tech.muyi.exception;

/**
 * 自定义的错误描述枚举类需实现该接口
 * @Author: muyi
 * @Date: 2021/1/3 21:27
 */
public interface BaseErrorInfoInterface {
    /** 错误码*/
    String getResultCode();

    /** 错误描述*/
    String getResultMsg();
}
