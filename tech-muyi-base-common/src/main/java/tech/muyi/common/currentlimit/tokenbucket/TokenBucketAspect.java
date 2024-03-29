package tech.muyi.common.currentlimit.tokenbucket;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import tech.muyi.common.MyResult;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 应用内令牌桶限流组件
 * @author: muyi
 * @date: 2021-01-16 20:02
 */
@Slf4j
@Aspect
@Component
public class TokenBucketAspect {

    /**
     * 存放不同方法的令牌
     */
    private final Map<String,RateLimiter> rateMap = new ConcurrentHashMap<>();

    @Pointcut("@annotation(tech.muyi.common.currentlimit.tokenbucket.TokenBucketAnnotation)")
    public void pointCut(){

    }

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            String methodName = methodSignature.getName();

            Method method = methodSignature.getMethod();

            TokenBucketAnnotation tokenBucketAnnotation = method.getAnnotation(TokenBucketAnnotation.class);

            if (tokenBucketAnnotation != null) {
                double capacity = tokenBucketAnnotation.capacity();
                long timeout = tokenBucketAnnotation.timeout();
                TimeUnit timeUnit = tokenBucketAnnotation.timeUnit();

                RateLimiter rateLimiter = null;
                if (!rateMap.containsKey(methodName)) {
                    rateLimiter = RateLimiter.create(capacity);
                    rateMap.put(methodName, rateLimiter);
                }

                rateLimiter = rateMap.get(methodName);

                if (rateLimiter.tryAcquire(timeout, timeUnit)) {
                    return joinPoint.proceed();
                }
                log.error("限流[{}]", methodName);
                return MyResult.fail(CommonErrorCodeEnum.CURRENT_LIMITING.getResultCode(), CommonErrorCodeEnum.CURRENT_LIMITING.getResultMsg());
            } else {
                log.error("限流未知异常[{}]", methodName);
                return MyResult.fail(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(), CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultMsg());
            }
        }catch (Exception e){
            log.error("限流未知异常[{}]", e);
            return MyResult.fail(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(), CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultMsg());
        }
    }
}
