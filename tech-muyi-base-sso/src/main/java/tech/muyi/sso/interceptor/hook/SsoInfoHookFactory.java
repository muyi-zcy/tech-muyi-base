package tech.muyi.sso.interceptor.hook;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tech.muyi.sso.dto.MySsoInfo;

import java.util.LinkedList;
import java.util.Map;

/**
 * SSO Hook 组装与执行器。
 *
 * <p>启动时收集容器内全部 `SsoInfoHook`，按注册顺序串行执行。</p>
 */
@Component
@ConditionalOnProperty(name = {"muyi.sso.enable"}, havingValue = "true")
public class SsoInfoHookFactory<T extends MySsoInfo> implements ApplicationContextAware {
    private LinkedList<SsoInfoHook<T>> ssoInfoHooks;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, SsoInfoHook> map = applicationContext.getBeansOfType(SsoInfoHook.class);
        ssoInfoHooks = new LinkedList<>();
        map.forEach((key, value) -> {
            ssoInfoHooks.add(value);
        });
    }


    public T exec(T mySsoInfo) {
        for (SsoInfoHook<T> ssoInfoHook : ssoInfoHooks) {
            mySsoInfo = ssoInfoHook.run(mySsoInfo);
        }
        return mySsoInfo;
    }

}
