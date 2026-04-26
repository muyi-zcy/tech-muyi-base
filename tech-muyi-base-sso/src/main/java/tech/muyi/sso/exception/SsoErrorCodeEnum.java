package tech.muyi.sso.exception;

import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;

/**
 * SSO 领域错误码定义。
 *
 * <p>当前预留枚举位，后续按鉴权、租户、会话等场景补充具体 code。</p>
 *
 * @author: muyi
 * @date: 2022/6/26
 **/

@ErrorCodeInfoAnno(
        name = "SSO错误码",
        parentCode = "base-error-code",
        code = "sso-error-code",
        desc = "SSO错误码-base")
public enum SsoErrorCodeEnum implements BaseErrorInfoInterface {



    
    ;

    /**
     * 错误码
     */
    private String resultCode;

    /**
     * 错误描述
     */
    private String resultMsg;

    SsoErrorCodeEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
