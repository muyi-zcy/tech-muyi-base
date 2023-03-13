package tech.muyi.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: muyi
 * @Date: 2021/1/4 23:15
 */
@Aspect
@Component
public class RestApiFilter {
    public RestApiFilter() {
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restApiAspect() {
    }

    @Around("restApiAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if ("/ok".equals(request.getRequestURI())) {
            return joinPoint.proceed();
        } else {
            Object target = joinPoint.getTarget();
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            String[] parameterNames = methodSignature.getParameterNames();
            Logger logger = LoggerFactory.getLogger(target.getClass());

            long startTime;
            StringBuilder logMsg;

            try {
                logMsg = new StringBuilder();
                logMsg.append("url:")
                        .append(request.getRequestURI())
                        .append(",REST METHOD ")
                        .append(joinPoint.getSignature().getName())
                        .append("()")
                        .append(" args:");

                Object[] args = joinPoint.getArgs();
                if (parameterNames != null && parameterNames.length != 0) {
                    for (int i = 0; i < parameterNames.length; ++i) {
                        if (!(args[i] instanceof HttpServletResponse) && !(args[i] instanceof MultipartFile) && !(args[i] instanceof HttpServletRequest)) {
                            logMsg.append(parameterNames[i]).append(":").append(JsonUtil.toJson(args[i])).append(",");
                        }
                    }
                } else {
                    logMsg.append("null ");
                }

                logger.info(logMsg.toString());
                startTime = System.currentTimeMillis();
            } catch (Exception e) {
                logger.error("print REST log error", e);
                return joinPoint.proceed();
            }

            Object object = joinPoint.proceed();

            try {
                logMsg = new StringBuilder();
                logMsg.append("Exit REST method ")
                        .append(joinPoint.getSignature().getName())
                        .append("()")
                        .append(" result:{},")
                        .append("use time:{}");

                logger.info(logMsg.toString(), JsonUtil.toJson(object), System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                logger.error("print REST log error", e);
            }
            return object;
        }
    }
}
