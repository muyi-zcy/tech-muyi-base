package tech.muyi.sso.interceptor.hook;

import tech.muyi.sso.dto.MySsoInfo;

public interface SsoInfoHook<T extends MySsoInfo> {

    T run(T mySsoInfo);
}
