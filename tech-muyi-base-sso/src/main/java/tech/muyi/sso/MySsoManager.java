package tech.muyi.sso;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import tech.muyi.redis.RedissonManage;
import tech.muyi.sso.dto.MySsoInfo;
import tech.muyi.sso.properties.MySsoProperties;
import tech.muyi.util.MyJson;
import tech.muyi.util.ttl.MyTransmittableThreadLocal;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: muyi
 * @date: 2023/9/24
 **/

public class MySsoManager<T extends MySsoInfo> {
    private MySsoProperties mySsoProperties;

    @Resource
    private RedissonManage redissonManage;

    private MyTransmittableThreadLocal<T> mySsoInfoTransmittableThreadLocal;

    public MySsoManager(MySsoProperties mySsoProperties) {
        this.mySsoProperties = mySsoProperties;
        mySsoInfoTransmittableThreadLocal = new MyTransmittableThreadLocal<>("sso", mySsoProperties.getSsoInfoClass());
    }

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
        return getCache(token, mySsoProperties.getSsoInfoClass());
    }

    private T getCache(String token, Class classT) {
        RBucket<String> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + token);
        String info = rBucket.get();
        if (StringUtils.isEmpty(info)) {
            return null;
        }

        return (T) MyJson.fromJson(info, classT);
    }


    public T getCache() {
        String token = this.getToken();
        return getCache(token);
    }

    public T cache(T mySsoInfo) {
        RBucket<String> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + mySsoInfo.getToken());
        rBucket.set(MyJson.toJson(mySsoInfo));
        rBucket.expireAt(mySsoInfo.getExpirationTime());
        return mySsoInfo;
    }

    public Boolean cleanCache(String token) {
        RBucket<String> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + token);
        return rBucket.delete();
    }

}
