package tech.muyi.exception;

/**
 * 自定义异常
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:25
 */
public class MyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected String errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public MyException() {
        super();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultCode());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public MyException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultCode(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = joinError(errorInfoInterface.getResultMsg(),cause);
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

    /**
     *
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    private String joinError(String errorMsg, String errorDetail) {
        return errorMsg.concat(";错误详情:").concat(errorDetail);
    }

    private String joinError(String errorMsg, Throwable cause) {
        if (cause != null && cause.getMessage() != null && !"".equals(cause.getMessage())) {
            return errorMsg.concat(";错误详情:").concat(cause.getMessage());
        }
        return errorMsg;
    }

}
