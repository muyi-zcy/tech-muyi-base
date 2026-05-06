package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 缓存异常
 *
 * <p>用于Redis等缓存操作失败的场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class CacheException extends SystemException {

    private String key;
    private String operation;

    public CacheException(String operation, String key, Throwable cause) {
        super(CommonErrorCodeEnum.INTERNAL_SERVER_ERROR,
                String.format("缓存操作失败: %s key=%s", operation, key),
                cause);
        this.operation = operation;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getOperation() {
        return operation;
    }
}
