package tech.muyi.sso;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import tech.muyi.redis.RedissonManage;
import tech.muyi.sso.dto.MySsoInfo;
import tech.muyi.sso.properties.MySsoProperties;

import javax.annotation.Resource;

/**
 * @author: muyi
 * @date: 2023/9/24
 **/
@Configuration
@ConditionalOnProperty(name = {"muyi.sso.enable"}, havingValue = "true")
public class MySsoManager<T extends MySsoInfo> {

    @Resource
    private MySsoProperties mySsoProperties;

    @Resource
    private RedissonManage redissonManage;

    private TransmittableThreadLocal<T> mySsoInfoTransmittableThreadLocal = new TransmittableThreadLocal<>();

    public void set(T ssoInfoDTO) {
        mySsoInfoTransmittableThreadLocal.set(ssoInfoDTO);
    }


    public void remove() {
        if (mySsoInfoTransmittableThreadLocal == null || mySsoInfoTransmittableThreadLocal.get() == null) {
            return;
        }
        mySsoInfoTransmittableThreadLocal.remove();
    }


    public T getSsoInfo() {
        if (mySsoInfoTransmittableThreadLocal == null) {
            return null;
        }
        return mySsoInfoTransmittableThreadLocal.get();
    }


    public String getSsoId() {
        T mySsoInfo = getSsoInfo();
        if (mySsoInfo == null) {
            return null;
        }
        return mySsoInfo.getSsoId();
    }

    public String getSsoName() {
        T ssoInfoDTO = getSsoInfo();
        if (ssoInfoDTO == null) {
            return null;
        }
        return ssoInfoDTO.getSsoName();
    }

    public String getToken() {
        T ssoInfoDTO = getSsoInfo();
        if (ssoInfoDTO == null) {
            return null;
        }
        return ssoInfoDTO.getToken();
    }


    public T getCache(String token) {
        RBucket<T> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + token);
        return rBucket.get();
    }

    public T cache(T mySsoInfo) {
        RBucket<T> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + mySsoInfo.getToken());
        rBucket.set(mySsoInfo);
        rBucket.expireAt(mySsoInfo.getExpirationTime());
        return mySsoInfo;
    }

    public Boolean cleanCache(String token) {
        RBucket<T> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + token);
        return rBucket.delete();
    }
}
