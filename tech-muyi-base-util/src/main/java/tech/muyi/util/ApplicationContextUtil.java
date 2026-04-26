package tech.muyi.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器静态访问入口。
 *
 * <p>用于在非 Spring 托管对象（或静态上下文）中按需取 Bean。
 * 为避免强耦合，业务代码优先使用构造器注入，仅在基础设施层兜底使用该工具。</p>
 *
 * @author: muyi
 * @date: 2022/11/29
 **/
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(String beanName, Class<T> glass)     {
        return applicationContext.getBean(beanName, glass);
    }

    public static <T> T getBean(Class<T> glass) {
        return applicationContext.getBean(glass);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        // synchronized 避免并发初始化时发生覆盖竞态。
        ApplicationContextUtil.applicationContext = applicationContext;
    }
}

