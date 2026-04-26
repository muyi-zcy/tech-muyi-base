package tech.muyi.core.config;

import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import tech.muyi.core.MyTracer;
import tech.muyi.core.async.config.MyLogAsyncBeanPostProcessor;
import tech.muyi.core.config.properties.MyLogProperties;
import tech.muyi.core.tracer.MyTracerBuilder;

/**
 * MyLog 核心自动配置。
 *
 * <p>注册全局 Tracer，并为异步执行器注入 trace 传播能力。</p>
 *
 * @author: muyi
 * @date: 2023/1/5
 **/
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(MyLogProperties.class)
public class MyLogAutoConfiguration {

    @Autowired
    private MyLogProperties myLogProperties;

    @Bean
    public MyTracer myTracer() {
        MyTracerBuilder myTracerBuilder = new MyTracerBuilder().withTags(myLogProperties.getTags());
        MyTracer myTracer =  myTracerBuilder.build();
        GlobalTracer.registerIfAbsent(myTracer);
        return myTracer;
    }

    @Bean
    @ConditionalOnClass({MyTracer.class, TaskDecorator.class})
    MyLogAsyncBeanPostProcessor myLogAsyncBeanPostProcessor(MyTracer myTracer) {
        return new MyLogAsyncBeanPostProcessor(myTracer);
    }
}
