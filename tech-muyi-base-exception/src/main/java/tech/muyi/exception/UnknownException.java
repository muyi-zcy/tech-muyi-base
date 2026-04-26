package tech.muyi.exception;


import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 未知异常包装。
 *
 * <p>用于将不可识别异常统一映射到 {@code UNKNOWN_EXCEPTION}，
 * 保持对外错误码口径一致，避免内部实现细节泄露。</p>
 *
 * @Author: muyi
 * @Date: 2021/1/3 22:15
 */
public class UnknownException extends MyException {
    public UnknownException(Throwable e) {
        super(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(), CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultMsg());
    }
}
