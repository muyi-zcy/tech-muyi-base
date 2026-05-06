package tech.muyi.exception;

import tech.muyi.exception.enumtype.ErrorLevel;

/**
 * 业务异常
 *
 * <p>继承自 MyException，专门用于业务场景</p>
 * <p>特点：可预期、可恢复、用户可理解，不填充堆栈</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class BizException extends MyException {

    public BizException(BaseErrorInfoInterface errorInfo) {
        super(errorInfo);
    }

    public BizException(BaseErrorInfoInterface errorInfo, String detail) {
        super(errorInfo, detail);
    }

    public BizException(BaseErrorInfoInterface errorInfo, Throwable cause) {
        super(errorInfo, cause);
    }

    public BizException(BaseErrorInfoInterface errorInfo, String detail, Throwable cause) {
        super(errorInfo, detail, cause);
    }

    @Override
    protected ErrorLevel getDefaultLevel() {
        return ErrorLevel.BIZ;
    }

    @Override
    protected boolean shouldFillStackTrace() {
        return false;
    }
}
