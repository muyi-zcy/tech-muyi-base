package tech.muyi.exception;

import lombok.extern.slf4j.Slf4j;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 统一业务异常。
 *
 * <p>约定：
 * <ul>
 *   <li>errorCode/errorMsg 为核心返回字段，优先保证 errorCode 稳定。</li>
 *   <li>构造器支持“错误码 + 详情/cause”拼接，便于日志追踪。</li>
 *   <li>{@link #fillInStackTrace()} 返回 this，降低高频业务异常抛出开销。</li>
 * </ul>
 *
 * <p>注意：关闭堆栈填充会减少定位上下文，关键链路建议同时记录完整日志。</p>
 *
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:25
 */
@Slf4j
public class MyException extends RuntimeException {
    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public MyException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, String errorDetail) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(), errorDetail);
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, String errorDetail, Throwable cause) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(), errorDetail, cause);
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultCode(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(), cause);
    }

    public MyException(Exception e) {
        super(e);
        this.errorCode = CommonErrorCodeEnum.INTERNAL_SERVER_ERROR.getResultCode();
        this.errorMsg = e.getMessage();
    }

    public MyException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public MyException(String errorCode, String errorMsg) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public MyException(String errorCode, String errorMsg, String errorDetail) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = joinError(errorMsg, errorDetail);
    }

    public MyException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.errorMsg = joinError(errorMsg, cause);
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        // 性能优化：不重新填充堆栈，避免在参数校验类异常场景产生大量开销。
        return this;
    }

    private String joinError(String errorMsg, String errorDetail) {
        return errorMsg.concat(";错误详情:").concat(errorDetail);
    }

    private String joinError(String errorMsg, Throwable cause) {
        if (cause != null && cause.getMessage() != null && !"".equals(cause.getMessage())) {
            // 兼容历史协议：错误详情通过分号拼接到主 message 中。
            return errorMsg.concat(";错误详情:").concat(cause.getMessage());
        }
        return errorMsg;
    }

    private String joinError(String errorMsg, String errorDetail, Throwable cause) {
        if (cause != null && cause.getMessage() != null && !"".equals(cause.getMessage())) {
            return errorMsg.concat(";错误详情:").concat("(").concat(errorDetail).concat(")").concat(cause.getMessage());
        }
        return errorMsg;
    }

}
