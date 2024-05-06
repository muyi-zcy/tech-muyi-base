package tech.muyi.sso.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;
import tech.muyi.sso.MySsoManager;
import tech.muyi.sso.dto.MySsoInfo;
import tech.muyi.sso.interceptor.hook.SsoInfoHookFactory;
import tech.muyi.sso.properties.MySsoProperties;
import tech.muyi.util.request.CookieUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@ConditionalOnProperty(name = {"muyi.sso.enable"}, havingValue = "true")
public class SsoInterceptor implements HandlerInterceptor {
    @Resource
    private MySsoProperties mySsoProperties;
    @Resource
    private MySsoManager mySsoManager;
    @Resource
    private SsoInfoHookFactory ssoInfoHookFactory;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 判断是否进行校验
        if (mySsoProperties == null || !mySsoProperties.isEnable()) {
            return true;
        }


        String token = getToken(request);
        if (token == null || token.isEmpty() || StringUtils.isEmpty(token)) {
            throw new MyException(CommonErrorCodeEnum.UNAUTHORIZED);
        }

        MySsoInfo mySsoInfo = mySsoManager.getCache(token);
        // 是否从redis中获取成功
        if (mySsoInfo == null) {
            throw new MyException(CommonErrorCodeEnum.UNAUTHORIZED.getResultCode(), mySsoProperties.getTag() + "不存在或已失效");
        }

        // 判断是否过期
        long currentTimeMillis = System.currentTimeMillis();
        if (mySsoInfo.getExpirationTime() != null && currentTimeMillis > mySsoInfo.getExpirationTime()) {
            throw new MyException(CommonErrorCodeEnum.UNAUTHORIZED.getResultCode(), mySsoProperties.getTag() + "已失效");
        }


        mySsoInfo = ssoInfoHookFactory.exec(mySsoInfo);

        // 判断是否延期
        // 计算时间差（毫秒级别）
        if (mySsoInfo.getExpirationTime() != null) {
            long timeDifference = mySsoInfo.getExpirationTime() - System.currentTimeMillis();
            if (timeDifference < 0) {
                mySsoManager.remove();
                throw new MyException(CommonErrorCodeEnum.UNAUTHORIZED.getResultCode(), mySsoProperties.getTag() + "不存在或已失效");
            }
            if (timeDifference < mySsoProperties.getEffectiveTime() / 2) {
                long expirationTime = System.currentTimeMillis() + mySsoProperties.getEffectiveTime();
                mySsoInfo.setExpirationTime(expirationTime);
                mySsoManager.cache(mySsoInfo);
            }
        }
        mySsoManager.set(mySsoInfo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        mySsoManager.remove();
    }

    private String getToken(HttpServletRequest request) {
        // 从请求头获取
        String token = request.getHeader(mySsoProperties.getTag());
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }

        // 从请求参数中获取
        token = request.getParameter(mySsoProperties.getTag());
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        // 从cookie获取
        return CookieUtils.getCookieValue(request, mySsoProperties.getTag());
    }
}
