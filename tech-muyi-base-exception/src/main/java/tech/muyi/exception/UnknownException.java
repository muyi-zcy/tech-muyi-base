package tech.muyi.exception;


import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:15
 */
public class UnknownException extends MyException {
    public UnknownException(Throwable e) {
        super(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(), CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultMsg(), e);
    }
}
