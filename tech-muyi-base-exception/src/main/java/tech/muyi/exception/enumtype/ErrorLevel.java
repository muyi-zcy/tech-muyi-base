package tech.muyi.exception.enumtype;

/**
 * 异常级别
 *
 * <p>用于区分不同类型的异常，决定是否填充堆栈和日志级别</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public enum ErrorLevel {
    /**
     * 业务级别：可预期的业务异常，不需要堆栈
     */
    BIZ,

    /**
     * 系统级别：不可预期的系统异常，需要堆栈
     */
    SYSTEM,

    /**
     * 未知级别：未分类的异常，需要堆栈
     */
    UNKNOWN
}
