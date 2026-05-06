package tech.muyi.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;
import tech.muyi.exception.enumtype.ErrorLevel;

/**
 * 统一业务异常基类（增强版）
 *
 * <p>增强内容：
 * <ul>
 *   <li>添加异常级别（ErrorLevel）</li>
 *   <li>根据异常级别决定是否填充堆栈</li>
 *   <li>保持向后兼容</li>
 * </ul>
 *
 * <p>注意：上下文信息请使用 MDC 或 SOFATracer Span Tag 记录</p>
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:25
 */
@Slf4j
@Getter
public class MyException extends RuntimeException {
    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;
    /**
     * 异常级别
     */
    protected ErrorLevel level;
    /**
     * 是否填充堆栈
     */
    protected boolean fillStackTrace;

    public MyException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
        this.level = getDefaultLevel();
        this.fillStackTrace = shouldFillStackTrace();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, String errorDetail) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(), errorDetail);
        this.level = getDefaultLevel();
        this.fillStackTrace = shouldFillStackTrace();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, String errorDetail, Throwable cause) {
        super(errorInfoInterface.getResultCode(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(), errorDetail, cause);
        this.level = getDefaultLevel();
        this.fillStackTrace = shouldFillStackTrace();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultCode(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(), cause);
        this.level = getDefaultLevel();
        this.fillStackTrace = shouldFillStackTrace();
    }

    public MyException(Exception e) {
        super(e);
        this.errorCode = CommonErrorCodeEnum.INTERNAL_SERVER_ERROR.getResultCode();
        this.errorMsg = e.getMessage();
        this.level = ErrorLevel.UNKNOWN;
        this.fillStackTrace = true;
    }

    public MyException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.level = ErrorLevel.BIZ;
        this.fillStackTrace = false;
    }

    public MyException(String errorCode, String errorMsg) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.level = ErrorLevel.BIZ;
        this.fillStackTrace = false;
    }

    public MyException(String errorCode, String errorMsg, String errorDetail) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = joinError(errorMsg, errorDetail);
        this.level = ErrorLevel.BIZ;
        this.fillStackTrace = false;
    }

    public MyException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.errorMsg = joinError(errorMsg, cause);
        this.level = ErrorLevel.SYSTEM;
        this.fillStackTrace = true;
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
        // 根据配置决定是否填充堆栈（优化后的逻辑）
        return fillStackTrace ? super.fillInStackTrace() : this;
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

    /**
     * 获取默认异常级别（子类可覆盖）
     */
    protected ErrorLevel getDefaultLevel() {
        return ErrorLevel.BIZ;
    }

    /**
     * 是否应该填充堆栈（子类可覆盖）
     */
    protected boolean shouldFillStackTrace() {
        // 默认不填充堆栈（保持原有性能优化）
        return false;
    }

}
