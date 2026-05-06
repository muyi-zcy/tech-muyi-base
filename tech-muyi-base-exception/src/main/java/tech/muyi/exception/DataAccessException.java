package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 数据访问异常
 *
 * <p>用于数据库操作失败的场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class DataAccessException extends SystemException {

    private String sql;

    public DataAccessException(String message, Throwable cause) {
        super(CommonErrorCodeEnum.DB_EXCEPTION, message, cause);
    }

    public DataAccessException(String message, String sql, Throwable cause) {
        super(CommonErrorCodeEnum.DB_EXCEPTION, message, cause);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
