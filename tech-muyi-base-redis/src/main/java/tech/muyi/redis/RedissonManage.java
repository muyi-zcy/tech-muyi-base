package tech.muyi.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tech.muyi.exception.MyException;
import tech.muyi.redis.exception.RedisErrorCodeEnum;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: muyi
 * @Date: 2021/1/3 10:14
 */

@Slf4j
@Configuration
public class RedissonManage {

    private String PREFIX;

    @Autowired
    private Environment environment;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 初始化
     * @param redissonClient redisson客户端
     */
    public RedissonManage(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取客户端
     * @return redis客户端
     */
    public RedissonClient getRedissonClient() {
        return this.redissonClient;
    }


    public String getKey(String key){
        if(this.PREFIX == null){
            this.PREFIX = Objects.requireNonNull(environment.getProperty("spring.application.name")).concat(":");
        }
        if(key == null || "".equals(key)){
            throw new MyException(RedisErrorCodeEnum.REDIS_KEY_NULL);
        }
        return this.PREFIX.concat(key);
    }

    /**
     * 获取分布式锁
     * @param lockKey key
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = this.redissonClient.getLock(getKey(lockKey));
        lock.lock();
        return lock;
    }

    /**
     * 获取分布式锁
     * @param lockKey key
     * @param leaseTime 续期时间
     * @return
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = this.redissonClient.getLock(getKey(lockKey));
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 获取分布式锁
     * @param lockKey key
     * @param unit 等待时间类型
     * @param leaseTime 续期时间
     * @return
     */
    public RLock lock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock lock = this.redissonClient.getLock(getKey(lockKey));
        lock.lock(leaseTime, unit);
        return lock;
    }

    /**
     * 获取分布式锁
     * @param lockKey key
     * @param unit 时间类型
     * @param waitTime 等待时间
     * @param leaseTime 续期时间
     * @return
     */
    public RLock tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = this.redissonClient.getLock(getKey(lockKey));

        try {
            lock.tryLock(waitTime, leaseTime, unit);
            return lock;
        } catch (InterruptedException e) {
            throw new MyException(e);
        }
    }
    /**
     * 获取分布式锁
     * @param lockKey key
     * @return
     */
    public boolean tryLock(String lockKey) {
        RLock lock = this.redissonClient.getLock(getKey(lockKey));
        return lock.tryLock();
    }


    /**
     * 解锁
     * @param lockKey key
     */
    public void unlock(String lockKey) {
        try {
            RLock lock = this.redissonClient.getLock(getKey(lockKey));
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            throw new MyException(e);
        }
    }

    /**
     * 解锁
     * @param lock 锁
     */
    public void unlock(RLock lock) {
        try {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            throw new MyException(e);
        }
    }

    /**
     * 设置对象
     * @param key key
     * @param value 对象值
     */
    public void set(String key, Object value) {
        RBucket<Object> re = this.redissonClient.getBucket(getKey(key));
        re.set(value);
    }

    /**
     *  设置对象并设置过期时间
     * @param key key
     * @param value 对象值
     * @param expire 过期时间
     */
    public void set(String key, Object value, long expire) {
        this.set(getKey(key), value, expire, TimeUnit.SECONDS);
    }

    /**
     *  设置对象并设置过期时间
     * @param key key
     * @param value 对象值
     * @param expire 过期时间
     * @param timeUnit 过期时间类型
     */
    public void set(String key, Object value, long expire, TimeUnit timeUnit) {
        RBucket<Object> re = this.redissonClient.getBucket(getKey(key));
        re.set(value, expire, timeUnit);
    }


    /**
     * 获取key对应值
     * @param key key
     * @param <T>
     * @return 对象
     */
    public <T> T get(String key) {
        RBucket<Object> re = this.redissonClient.getBucket(getKey(key));
        Object o = re.get();
        return o == null ? null : (T) o;
    }


    /**
     * 删除key
     * @param key
     * @return
     */
    public Boolean delete(String key) {
        return this.redissonClient.getBucket(getKey(key)).delete();
    }


    /**
     * 添加List元素
     * @param key key
     * @param value 对象值
     */
    public void addListElement(String key, Object value) {
        RList<Object> list = this.redissonClient.getList(getKey(key));
        list.add(value);
    }

    /**
     * 添加List元素
     * @param key key
     * @param value 对象值
     * @param expire 过期时间
     */
    public void addListElement(String key, Object value, long expire) {
        RList<Object> list = this.redissonClient.getList(getKey(key));
        list.add(value);
        list.expire(expire, TimeUnit.SECONDS);
    }


    /**
     * 批量添加List元素
     * @param key key
     * @param value 对象值
     */
    public void addListAllElement(String key, Collection value) {
        RList<Object> list = this.redissonClient.getList(getKey(key));
        list.addAll(value);
    }

    /**
     * 批量添加List元素
     * @param key key
     * @param value 对象值
     * @param expire 过期时间
     */
    public void addListAllElement(String key, Collection value, long expire) {
        RList<Object> list = this.redissonClient.getList(getKey(key));
        list.addAll(value);
        list.expire(expire, TimeUnit.SECONDS);
    }

    /**
     * 获取整个List
     * @param key key
     * @return 整个List
     */
    public <V> List<V> getList(String key) {
        RList list = this.redissonClient.getList(getKey(key));
        return list == null ? null : list;
    }

    /**
     * 获取List某个元素
     * @param key key
     * @param index 序号
     * @return 元素
     */
    public <T> T getListOneElement(String key, int index) {
        RList list = this.redissonClient.getList(getKey(key));
        return list == null ? null : (T) list.get(index);
    }

    /**
     * 移除List某一个元素
     * @param key
     * @param value
     * @return
     */
    public Boolean removeListOneElement(String key, Object value) {
        RList list = this.redissonClient.getList(getKey(key));
        return list.remove(value);
    }

    /**
     * 删除List
     * @param key key
     * @return
     */
    public Boolean deleteList(String key) {
        return this.redissonClient.getList(getKey(key)).delete();
    }


    /**
     * 原子类型增加
     * @param key key
     * @return
     */
    public Long incrby(String key) {
        return this.redissonClient.getAtomicLong(getKey(key)).incrementAndGet();
    }

    /**
     * 原子类型减少
     * @param key
     * @return
     */
    public Long decrby(String key) {
        return this.redissonClient.getAtomicLong(getKey(key)).decrementAndGet();
    }

    /**
     * 原子类型增加
     * @param key key
     * @param delta 增量
     * @return
     */
    public Long incrby(String key, Long delta) {
        return this.redissonClient.getAtomicLong(getKey(key)).addAndGet(delta);
    }

    /**
     * 原子类型增加
     * @param key key
     * @param delta 增量
     * @param expire 过期时间
     * @return
     */
    public Long incrby(String key, Long delta, Long expire) {
        RAtomicLong rAtomicLong = this.redissonClient.getAtomicLong(getKey(key));
        long value = rAtomicLong.addAndGet(delta);
        rAtomicLong.expire(expire, TimeUnit.MILLISECONDS);
        return value;
    }
    /**
     * 原子类型增加
     * @param key key
     * @param delta 增量
     * @param expire 过期时间
     * @param unit 过期时间类型
     * @return
     */
    public Long incrby(String key, Long delta, Long expire, TimeUnit unit) {
        RAtomicLong rAtomicLong = this.redissonClient.getAtomicLong(getKey(key));
        long value = rAtomicLong.addAndGet(delta);
        rAtomicLong.expire(expire, unit);
        return value;
    }

    /**
     * 初始化过滤器
     * @param key key
     * @param expectedInsertions 预期的插入
     * @param falseProbability 概率
     * @return 结果
     */
    public boolean initBloomFilter(String key, Long expectedInsertions, double falseProbability){
        RBloomFilter<Object> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.tryInit(expectedInsertions,falseProbability);
    }

    /**
     * 存入过滤器
     * @param key key
     * @param value 存入值
     * @return 结果
     */
    public <T> boolean addIndexBloomFilter(String key, T value){
        RBloomFilter<T> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.add(value);
    }


    /**
     * 是否包含过滤器
     * @param key key
     * @param value 判断值
     * @return 结果
     */
    public <T> boolean containIndexBloomFilter(String key, T value){
        RBloomFilter<T> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.contains(value);
    }

    /**
     * 删除值过滤器
     * @param key key
     * @param value 待删除值
     * @return 结果
     */
    public <T> boolean deleteBloomFilter(String key, T value){
        RBloomFilter<T> bloomFilter = this.redissonClient.getBloomFilter(key);
        return bloomFilter.delete();
    }
}
