package tech.muyi.common.currentlimit.tokenbucket;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: muyi
 * @date: 2021-01-16 19:55
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenBucketAnnotation {

    /**
     * 每秒创建令牌数,即流量大小
     * @return
     */
    double capacity() default 10D;

    /**
     * 超时时间
     * @return
     */
    long timeout() default 0;

    /**
     * 超时时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
