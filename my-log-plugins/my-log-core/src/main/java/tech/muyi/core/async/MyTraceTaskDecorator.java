package tech.muyi.core.async;

import com.alibaba.ttl.TtlRunnable;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import tech.muyi.core.MyTracer;
import tech.muyi.util.ApplicationContextUtil;

import javax.annotation.Resource;

/**
 * @author: muyi
 * @date: 2023/1/17
 **/
public class MyTraceTaskDecorator implements TaskDecorator {

    private final TaskDecorator taskDecorator;

    private final MyTracer myTracer;

    public MyTraceTaskDecorator(TaskDecorator taskDecorator,MyTracer myTracer) {
        this.taskDecorator = taskDecorator;
        this.myTracer = myTracer;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        try {
            runnable =  MyTraceRunnable.get(runnable, new MyTraceAsyncSupport(myTracer));
            return taskDecorator.decorate(runnable);
        } catch (IllegalStateException e) {
            return runnable;
        }
    }
}
