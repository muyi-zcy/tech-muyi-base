package tech.muyi.redis;

import io.opentracing.contrib.redis.common.TracingConfiguration;
import io.opentracing.contrib.redis.redisson.TracingRedissonClient;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import tech.muyi.core.MyTracer;
import tech.muyi.core.tracer.MyTracerBuilder;

/**
 * @author: muyi
 * @date: 2023/1/13
 **/

public class MyLogRedisBeanPostProcessor implements BeanPostProcessor {

    private final MyTracer myTracer;

    public MyLogRedisBeanPostProcessor(MyTracer myTracer) {
        this.myTracer = myTracer;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RedissonClient) {
            bean = new TracingRedissonClient((RedissonClient) bean, new TracingConfiguration.Builder(myTracer).build());
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
