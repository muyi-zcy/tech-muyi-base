package tech.muyi.id.snowflake;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RuntimeUtil;
import tech.muyi.id.MyIdGenerator;
import tech.muyi.id.properties.MyIdSnowflakeProperties;

/**
 * 默认雪花ID，使用hutool的雪花算法，方便定义初始化时间，直接拷贝重写
 *
 * @author: muyi
 * @date: 2023/1/11
 **/
public class DefaultSnowflakeImpl implements MyIdGenerator {


    // 基准时间
    public static long DEFAULT_TWEPOCH = 1672502400000L;


    /**
     * 时间偏移量、默认回拨时间，2S
     */
    public static long DEFAULT_TIME_OFFSET = 2000L;

    private static final long WORKER_ID_BITS = 5L;
    // 最大支持机器节点数0~31，一共32个
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    private static final long DATA_CENTER_ID_BITS = 5L;
    // 最大支持数据中心节点数0~31，一共32个
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);
    // 序列号12位（表示只允许workId的范围为：0-4095）
    private static final long SEQUENCE_BITS = 12L;
    // 机器节点左移12位
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 数据中心节点左移17位
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    // 时间毫秒数左移22位
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    // 序列掩码，用于限定序列最大值不能超过4095
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);// 4095

    private final long twepoch;
    private final long workerId;
    private final long dataCenterId;
    private final boolean useSystemClock;
    private final long timeOffset;
    private long sequence;
    private long lastTimestamp;


    public DefaultSnowflakeImpl(MyIdSnowflakeProperties myIdSnowflakeProperties) {

        if (myIdSnowflakeProperties != null && myIdSnowflakeProperties.getBaseTime() != null) {
            this.twepoch = myIdSnowflakeProperties.getBaseTime();
        } else {
            this.twepoch = DEFAULT_TWEPOCH;
        }

        if (myIdSnowflakeProperties != null && myIdSnowflakeProperties.getTimeOffs() != null) {
            this.timeOffset = myIdSnowflakeProperties.getTimeOffs();
        } else {
            this.timeOffset = DEFAULT_TIME_OFFSET;
        }

        if (myIdSnowflakeProperties != null) {
            this.useSystemClock = myIdSnowflakeProperties.isUseSystemClock();
        } else {
            this.useSystemClock = false;
        }

        if (myIdSnowflakeProperties == null || myIdSnowflakeProperties.getDataCenterId() == null) {
            this.dataCenterId = initDataCenterId(MAX_DATA_CENTER_ID);
        } else {
            this.dataCenterId = myIdSnowflakeProperties.getDataCenterId();
        }

        if (myIdSnowflakeProperties == null || myIdSnowflakeProperties.getWorkerId() == null) {
            this.workerId = initWorkerId(this.dataCenterId, MAX_WORKER_ID);
        } else {
            this.workerId = myIdSnowflakeProperties.getWorkerId();
        }

        if (workerId > 31L || workerId < 0L) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %s or less than 0", 31L));
        }

        if (dataCenterId > 31L || dataCenterId < 0L) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %s or less than 0", 31L));
        }

    }

    @Override
    public void init() {

    }

    /**
     * 根据Snowflake的ID，获取机器id
     *
     * @param id snowflake算法生成的id
     * @return 所属机器的id
     */
    public long getWorkerId(long id) {
        return id >> WORKER_ID_SHIFT & ~(-1L << WORKER_ID_BITS);
    }

    /**
     * 根据Snowflake的ID，获取数据中心id
     *
     * @param id snowflake算法生成的id
     * @return 所属数据中心
     */
    public long getDataCenterId(long id) {
        return id >> DATA_CENTER_ID_SHIFT & ~(-1L << DATA_CENTER_ID_BITS);
    }

    /**
     * 根据Snowflake的ID，获取生成时间
     *
     * @param id snowflake算法生成的id
     * @return 生成的时间
     */
    public long getGenerateDateTime(long id) {
        return (id >> TIMESTAMP_LEFT_SHIFT & ~(-1L << 41L)) + twepoch;
    }

    /**
     * 下一个ID
     *
     * @return ID
     */
    public synchronized long nextId() {
        long timestamp = genTime();
        if (timestamp < this.lastTimestamp) {
            if (this.lastTimestamp - timestamp < timeOffset) {
                // 容忍指定的回拨，避免NTP校时造成的异常
                timestamp = lastTimestamp;
            } else {
                // 如果服务器时间有问题(时钟后退) 报错。
                throw new IllegalStateException(String.format("Clock moved backwards. Refusing to generate id for %s ms", lastTimestamp - timestamp));
            }
        }

        if (timestamp == this.lastTimestamp) {
            final long sequence = (this.sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
            this.sequence = sequence;
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 下一个ID（字符串形式）
     *
     * @return ID 字符串形式
     */
    public String nextIdStr() {
        return Long.toString(nextId());
    }

    // ------------------------------------------------------------------------------------------------------------------------------------ Private method start

    /**
     * 循环等待下一个时间
     *
     * @param lastTimestamp 上次记录的时间
     * @return 下一个时间
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = genTime();
        // 循环直到操作系统时间戳变化
        while (timestamp == lastTimestamp) {
            timestamp = genTime();
        }
        if (timestamp < lastTimestamp) {
            // 如果发现新的时间戳比上次记录的时间戳数值小，说明操作系统时间发生了倒退，报错
            throw new IllegalStateException(
                    String.format("Clock moved backwards. Refusing to generate id for %s ms", lastTimestamp - timestamp));
        }
        return timestamp;
    }

    /**
     * 生成时间戳
     *
     * @return 时间戳
     */
    private long genTime() {
        return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }

    public long initDataCenterId(long maxDatacenterId) {
        long id = 1L;
        byte[] mac = NetUtil.getLocalHardwareAddress();
        if (null != mac) {
            id = (255L & (long) mac[mac.length - 2] | 65280L & (long) mac[mac.length - 1] << 8) >> 6;
            id %= maxDatacenterId + 1L;
        }

        return id;
    }

    public long initWorkerId(long datacenterId, long maxWorkerId) {
        String mpid = String.valueOf(datacenterId) + RuntimeUtil.getPid();
        return (mpid.hashCode() & 0xffff) % (maxWorkerId + 1);
    }

}
