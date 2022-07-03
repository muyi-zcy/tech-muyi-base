package tech.muyi.redis.exception;

import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;

/**
 * @author: muyi
 * @date: 2022/6/26
 **/
@ErrorCodeInfoAnno(name = "非关系型数据库(redis)错误码",parentCode = "cache-error-code", code = "cache-redis-error-code", desc = "非关系型数据库错误码-redis")
public enum RedisErrorCodeEnum implements BaseErrorInfoInterface {
    REDIS_KEY_NULL("","")
    ;

    /** 错误码 */
    private String resultCode;

    /** 错误描述 */
    private String resultMsg;

    RedisErrorCodeEnum(String resultCode, String resultMsg) {
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
