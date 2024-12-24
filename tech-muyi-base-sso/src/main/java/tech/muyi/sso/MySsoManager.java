package tech.muyi.sso;


import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import tech.muyi.redis.RedissonManage;
import tech.muyi.sso.dto.MySsoInfo;
import tech.muyi.sso.properties.MySsoProperties;
import tech.muyi.sso.properties.MyTenantProperties;
import tech.muyi.util.MyJson;
import tech.muyi.util.ttl.MyTransmittableThreadLocal;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2023/9/24
 **/

public class MySsoManager<T extends MySsoInfo> {
    private MySsoProperties mySsoProperties;

    private MyTenantProperties myTenantProperties;

    @Resource
    private RedissonManage redissonManage;


    private final MyTransmittableThreadLocal<T> mySsoInfoTransmittableThreadLocal;

    public MySsoManager(MySsoProperties mySsoProperties, MyTenantProperties myTenantProperties) {
        this.mySsoProperties = mySsoProperties;
        this.myTenantProperties = myTenantProperties;
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

        T data = (T) MyJson.fromJson(info, classT);
        data.setCacheValue(info);
        return data;
    }


    public T getCache() {
        String token = this.getToken();
        return getCache(token);
    }

    public T cache(T mySsoInfo) {
        return this.cache(mySsoInfo, mySsoInfo.getExpirationTime());
    }

    public T cache(T mySsoInfo, LocalDateTime expireAt) {
        RBucket<String> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + mySsoInfo.getToken());
        if (StringUtils.isNotEmpty(mySsoInfo.getCacheValue())) {
            Map<String, Object> cacheValue = MyJson.getGson().fromJson(mySsoInfo.getCacheValue(), new TypeToken<HashMap<String, Object>>() {
            }.getType());
            cacheValue.put("expirationTime", mySsoInfo.getExpirationTime());
            rBucket.set(MyJson.toJson(cacheValue));
        } else {
            rBucket.set(MyJson.toJson(mySsoInfo));
        }
        if (expireAt != null) {
            rBucket.expireAt(expireAt.toEpochSecond(ZoneOffset.UTC) * 1000L);
        }
        return mySsoInfo;
    }

    public Boolean cleanCache(String token) {
        RBucket<String> rBucket = redissonManage.getRedissonClient().getBucket(mySsoProperties.getTokenKey() + token);
        return rBucket.delete();
    }

    public Boolean isSuperTenant() {
        T mySsoInfo = getSsoInfo();
        if (mySsoInfo == null) {
            return false;
        }

        if (myTenantProperties == null) {
            return true;
        }

        if (myTenantProperties.getSuperTenantId() == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(myTenantProperties.getSuperTenantId())) {
            return false;
        }
        return myTenantProperties.getSuperTenantId().contains(mySsoInfo.getTenantId());
    }

}
