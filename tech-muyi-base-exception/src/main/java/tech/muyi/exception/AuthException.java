package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 认证授权异常
 *
 * <p>用于认证、授权失败场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class AuthException extends BizException {

    public AuthException(String message) {
        super(CommonErrorCodeEnum.PERMISSION_NO_ACCESS, message);
    }

    public AuthException(BaseErrorInfoInterface errorInfo) {
        super(errorInfo);
    }

    public AuthException(BaseErrorInfoInterface errorInfo, String detail) {
        super(errorInfo, detail);
    }
}
