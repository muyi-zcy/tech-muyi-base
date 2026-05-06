package tech.muyi.exception;

import tech.muyi.exception.enumtype.ErrorLevel;

/**
 * 系统异常
 *
 * <p>继承自 MyException，专门用于系统级异常</p>
 * <p>特点：不可预期、需要人工介入、技术性错误，填充堆栈</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class SystemException extends MyException {

    public SystemException(BaseErrorInfoInterface errorInfo) {
        super(errorInfo);
    }

    public SystemException(BaseErrorInfoInterface errorInfo, String detail) {
        super(errorInfo, detail);
    }

    public SystemException(BaseErrorInfoInterface errorInfo, Throwable cause) {
        super(errorInfo, cause);
    }

    public SystemException(BaseErrorInfoInterface errorInfo, String detail, Throwable cause) {
        super(errorInfo, detail, cause);
    }

    @Override
    protected ErrorLevel getDefaultLevel() {
        return ErrorLevel.SYSTEM;
    }

    @Override
    protected boolean shouldFillStackTrace() {
        return true;
    }
}
