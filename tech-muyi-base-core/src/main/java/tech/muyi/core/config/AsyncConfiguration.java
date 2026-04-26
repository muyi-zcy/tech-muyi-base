package tech.muyi.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tech.muyi.core.async.MyTaskDecorator;
import java.util.concurrent.Executor;

/**
 * 异步执行与调度的统一配置。
 *
 * <p>约束与目的：
 * <ul>
 *   <li>线程池参数统一来自 Spring Boot 的 {@link TaskExecutionProperties}，避免散落配置导致容量不可控。</li>
 *   <li>通过 {@link TaskDecorator}（默认 {@link MyTaskDecorator}）在异步执行时传播上下文（request/ThreadLocal）。</li>
 *   <li>同时启用 {@link EnableAsync} 与 {@link EnableScheduling}，使 @Async 与定时任务在同一模块内可用。</li>
 * </ul>
 *
 * @author: muyi
 * @date: 2022/11/29
 **/
@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer {
    private final TaskExecutionProperties taskExecutionProperties;

    private TaskDecorator taskDecorator;

    public AsyncConfiguration(TaskExecutionProperties taskExecutionProperties) {
        this.taskExecutionProperties = taskExecutionProperties;
    }

    @Bean
    public TaskDecorator myTaskDecorator(){
        // 单独声明为 Bean：便于在其它地方复用/替换，同时避免在 getAsyncExecutor() 中直接 new 导致不可测试。
        TaskDecorator taskDecorator = new MyTaskDecorator();
        this.taskDecorator = taskDecorator;
        return taskDecorator;
    }

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        log.debug("Creating Async Task Executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池容量策略由配置文件统一控制（core/max/queue），避免线上出现默认值过小或过大的问题。
        executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());
        executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());
        executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity());
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
        // 装饰器用于跨线程传播上下文；如果为 null，则只是不传播，不影响任务执行。
        executor.setTaskDecorator(taskDecorator);
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // 对于返回类型为 void 的 @Async 方法，异常不会被 Future 携带，这里提供统一的兜底处理策略。
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}