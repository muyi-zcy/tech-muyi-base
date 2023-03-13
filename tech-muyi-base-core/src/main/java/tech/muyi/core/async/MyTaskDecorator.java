package tech.muyi.core.async;

import com.alibaba.ttl.TtlRunnable;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author: muyi
 * @date: 2023/1/17
 **/
public class MyTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return TtlRunnable.get(()->{
                try {
                    RequestContextHolder.setRequestAttributes(attributes);
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            });
        } catch (IllegalStateException e) {
            return runnable;
        }
    }
}
