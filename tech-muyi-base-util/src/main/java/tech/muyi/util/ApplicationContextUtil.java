package tech.muyi.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: muyi
 * @date: 2022/11/29
 **/
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(String beanName, Class<T> glass) {
        return applicationContext.getBean(beanName, glass);
    }

    public static <T> T getBean(Class<T> glass) {
        return applicationContext.getBean(glass);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextUtil.applicationContext = applicationContext;
    }
}

