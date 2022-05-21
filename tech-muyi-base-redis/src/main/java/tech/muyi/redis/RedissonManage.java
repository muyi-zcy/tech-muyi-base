package tech.muyi.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: muyi
 * @Date: 2021/1/3 10:14
 */

@Slf4j
public class RedissonManage {
    private RedissonClient redissonClient;
    private String PREFIX = "RDS_LOCK:" + System.getenv("project.name") + ":";

    public RedissonManage(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取客户端
     * @return
     */
    public RedissonClient getRedissonClient() {
        return this.redissonClient;
    }

    public RLock lock(String lockKey) {
        RLock lock = this.redissonClient.getLock(this.PREFIX + lockKey);
        lock.lock();
        return lock;
    }

    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = this.redissonClient.getLock(this.PREFIX + lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    public RLock lock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock lock = this.redissonClient.getLock(this.PREFIX + lockKey);
        lock.lock(leaseTime, unit);
        return lock;
    }

    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = this.redissonClient.getLock(this.PREFIX + lockKey);

        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            log.warn("获取锁失败:lockKey【{}】", lockKey);
            return false;
        }
    }

    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime) {
        RLock lock = this.redissonClient.getLock(this.PREFIX + lockKey);

        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            log.warn("获取锁失败:lockKey【{}】", lockKey);
            return false;
        }
    }

    public void unlock(String lockKey) {
        try {
            RLock lock = this.redissonClient.getLock(this.PREFIX + lockKey);
            lock.unlock();
        } catch (Exception e) {
        }
    }

    public void unlock(RLock lock) {
        try {
            lock.unlock();
        } catch (Exception e) {
        }
    }

    public void set(String key, Object value) {
        RBucket<Object> re = this.redissonClient.getBucket(key);
        re.set(value);
    }

    public void set(String key, Object value, long expire) {
        this.set(key, value, expire, TimeUnit.SECONDS);
    }

    public void set(String key, Object value, long expire, TimeUnit timeUnit) {
        RBucket<Object> re = this.redissonClient.getBucket(key);
        re.set(value, expire, timeUnit);
    }

    public void addElement(String key, Object value, long expire) {
        RList<Object> list = this.redissonClient.getList(key);
        list.add(value);
        list.expire(expire, TimeUnit.SECONDS);
    }

    public void addAllElement(String key, Collection value, long expire) {
        RList<Object> list = this.redissonClient.getList(key);
        list.addAll(value);
        list.expire(expire, TimeUnit.SECONDS);
    }

    public <T> T get(String key) {
        RBucket<Object> re = this.redissonClient.getBucket(key);
        Object o = re.get();
        return o == null ? null : (T) o;
    }

    public <V> List<V> getList(String key) {
        RList list = this.redissonClient.getList(key);
        return list == null ? null : list;
    }

    public <T> T getOneElement(String key, int index) {
        RList list = this.redissonClient.getList(key);
        return list == null ? null : (T) list.get(index);
    }

    public Boolean delete(String k) {
        return this.redissonClient.getBucket(k).delete();
    }

    public Boolean deleteList(String k) {
        return this.redissonClient.getList(k).delete();
    }

    public Boolean removeElement(String k, long expire, Object value) {
        RList list = this.redissonClient.getList(k);
        boolean b = list.remove(value);
        if (b) {
            list.expire(expire, TimeUnit.SECONDS);
        }

        return b;
    }

    public Long incrby(String k, Long delta) {
        return this.redissonClient.getAtomicLong(k).addAndGet(delta);
    }

    public Long incrby(String k, Long delta, Long expire, TimeUnit unit) {
        RAtomicLong rAtomicLong = this.redissonClient.getAtomicLong(k);
        long value = rAtomicLong.addAndGet(delta);
        rAtomicLong.expire(expire, unit);
        return value;
    }

    public Boolean isMemers(String key, String value) {
        return this.redissonClient.getSet(key).contains(value);
    }

    public Boolean addSet(String key, String value, Long seconds) {
        RSet rSet = this.redissonClient.getSet(key);
        boolean b = rSet.contains(value);
        if (!b) {
            rSet.add(b);
            rSet.expire(seconds, TimeUnit.SECONDS);
            return true;
        } else {
            return false;
        }
    }

    public boolean initBloomFilter(String key,Long expectedInsertions,double falseProbability){
        RBloomFilter<Object> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.tryInit(expectedInsertions,falseProbability);
    }

    public <T> boolean addIndexBloomFilter(String key, T value){
        RBloomFilter<T> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.add(value);
    }


    public <T> boolean containIndexBloomFilter(String key, T value){
        RBloomFilter<T> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.contains(value);
    }

    public <T> boolean deleteBloomFilter(String key, T value){
        RBloomFilter<T> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.delete();
    }
}
