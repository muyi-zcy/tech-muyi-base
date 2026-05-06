package tech.muyi.redis;

import com.alipay.common.tracer.core.SofaTracer;
import com.alipay.common.tracer.core.context.trace.SofaTraceContext;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.redisson.api.RFuture;
import org.redisson.client.RedisConnection;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.command.CommandAsyncService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Redisson 命令拦截器
 * 基于 SOFATracer 实现
 *
 * @author muyi
 */
public class RedissonCommandInterceptor implements InvocationHandler {

    private final Object target;
    private static final String OPERATION_NAME = "Redisson/command";

    public RedissonCommandInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SofaTracer sofaTracer = RedissonTracer.getSofaTracer();
        if (sofaTracer == null) {
            return method.invoke(target, args);
        }

        // 只追踪异步命令执行方法
        if (!method.getName().equals("async") && !method.getName().startsWith("read")
                && !method.getName().startsWith("write")) {
            return method.invoke(target, args);
        }

        Span span = null;
        try {
            // 构建 Span
            span = sofaTracer.buildSpan(OPERATION_NAME)
                    .withTag(Tags.COMPONENT.getKey(), "Redisson")
                    .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                    .withTag(Tags.DB_TYPE.getKey(), "redis")
                    .withTag("redis.method", method.getName())
                    .start();

            // 推入上下文栈
            if (span instanceof SofaTracerSpan) {
                SofaTraceContext traceContext = SofaTraceContextHolder.getSofaTraceContext();
                if (traceContext != null) {
                    traceContext.push((SofaTracerSpan) span);
                }
            }

            // 执行原方法
            Object result = method.invoke(target, args);

            // 如果返回 RFuture，添加完成回调
            if (result instanceof RFuture) {
                RFuture<?> future = (RFuture<?>) result;
                Span finalSpan = span;
                future.whenComplete((r, ex) -> {
                    try {
                        if (ex != null) {
                            finalSpan.setTag(Tags.ERROR.getKey(), true);
                            finalSpan.setTag("error.message", ex.getMessage());
                        }
                    } finally {
                        finishSpan(finalSpan);
                    }
                });
            } else {
                finishSpan(span);
            }

            return result;
        } catch (Exception e) {
            if (span != null) {
                span.setTag(Tags.ERROR.getKey(), true);
                span.setTag("error.message", e.getMessage());
                finishSpan(span);
            }
            throw e;
        }
    }

    private void finishSpan(Span span) {
        if (span instanceof SofaTracerSpan) {
            SofaTraceContext traceContext = SofaTraceContextHolder.getSofaTraceContext();
            if (traceContext != null) {
                traceContext.pop();
            }
        }
        span.finish();
    }

    /**
     * 创建代理对象
     */
    public static CommandAsyncExecutor createProxy(CommandAsyncExecutor target) {
        return (CommandAsyncExecutor) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new RedissonCommandInterceptor(target)
        );
    }
}
