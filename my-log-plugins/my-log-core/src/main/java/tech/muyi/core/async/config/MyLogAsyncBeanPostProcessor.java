package tech.muyi.core.async.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.task.TaskDecorator;
import tech.muyi.core.MyTracer;
import tech.muyi.core.async.MyTraceTaskDecorator;

/**
 * @author: muyi
 * @date: 2023/1/17
 **/
public class MyLogAsyncBeanPostProcessor implements BeanPostProcessor {

    private final MyTracer myTracer;

    public MyLogAsyncBeanPostProcessor(MyTracer myTracer) {
        this.myTracer = myTracer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskDecorator) {
            bean = new MyTraceTaskDecorator((TaskDecorator) bean, myTracer);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
