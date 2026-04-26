package tech.muyi.sso.interceptor.hook;

import tech.muyi.sso.dto.MySsoInfo;

/**
 * SSO 信息扩展钩子。
 *
 * <p>用于在拦截器完成基础校验后，对会话信息做二次补充或校验。</p>
 */
public interface SsoInfoHook<T extends MySsoInfo> {

    T run(T mySsoInfo);
}
