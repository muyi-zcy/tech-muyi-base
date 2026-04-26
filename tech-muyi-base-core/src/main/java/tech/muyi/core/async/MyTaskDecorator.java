package tech.muyi.core.async;

import com.alibaba.ttl.TtlRunnable;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Spring 异步线程池任务装饰器：将 Web 请求上下文（{@link RequestAttributes}）从提交线程传播到执行线程。
 *
 * <p>使用点：
 * <ul>
 *   <li>配合 {@link org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#setTaskDecorator(TaskDecorator)}
 *       在 @Async / 线程池执行场景下保留请求上下文能力（如获取 request scope 属性、租户信息等）。</li>
 *   <li>同时使用 Alibaba TTL（{@link TtlRunnable}）传播 ThreadLocal（如 MDC / 自定义上下文）以减少“异步丢上下文”。</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>必须在 finally 中清理 {@link RequestContextHolder}，否则线程复用会导致上下文串扰。</li>
 *   <li>无 Web 请求上下文（非 Web 线程/启动阶段/定时任务等）时，{@link RequestContextHolder#getRequestAttributes()}
 *       可能触发 {@link IllegalStateException}；此时直接返回原 runnable 以保持兼容。</li>
 * </ul>
 *
 * @author: muyi
 * @date: 2023/1/17
 **/
public class MyTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        try {
            // 捕获“提交任务的线程”中的请求上下文快照；后续会在异步执行线程中重新绑定。
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            // 用 TTL 包装，确保其它 ThreadLocal（例如链路追踪/MDC、租户上下文等）也能跨线程传播。
            return TtlRunnable.get(() -> {
                try {
                    // 将请求上下文绑定到当前执行线程，保证业务代码读取 request scope 时一致。
                    RequestContextHolder.setRequestAttributes(attributes);
                    runnable.run();
                } finally {
                    // 线程池线程会复用：必须清理，避免下一个任务“继承”上一个请求的上下文。
                    RequestContextHolder.resetRequestAttributes();
                }
            });
        } catch (IllegalStateException e) {
            // 非 Web 请求线程等场景下不强制传播请求上下文，保持任务可执行。
            return runnable;
        }
    }
}
