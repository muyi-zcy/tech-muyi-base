package tech.muyi.redis.exception;

import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;

/**
 * @author: muyi
 * @date: 2022/6/26
 **/

@ErrorCodeInfoAnno(name = "非关系型数据库错误码",parentCode = "base-error-code", code = "cache-error-code", desc = "非关系型数据库错误码-base")
public enum CacheErrorCodeEnum implements BaseErrorInfoInterface {
    ADDRESS_ERROR("4011","数据库地址错误"),
    PASSWORD_ERROR("4012","数据库密码错误"),
    NO_CONNECT("4013","无法从连接池获取到连接"),
    CONNECT_TIME_OUT("4014","连接超时"),
    READ_TIME_OUT("4015","读写超时"),
    ;

    /** 错误码 */
    private String resultCode;

    /** 错误描述 */
    private String resultMsg;

    CacheErrorCodeEnum(String resultCode, String resultMsg) {
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
