package tech.muyi.log.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import tech.muyi.log.LogRecordContext;
import tech.muyi.log.annotation.MyTraceIdCreate;

import java.lang.reflect.Method;

/**
 * description: TraceIdCreateAspect
 * date: 2022/1/2 23:04
 * author: muyi
 * version: 1.0
 */
@Aspect
@Component
public class TraceIdCreateAspect {
    public TraceIdCreateAspect() {
    }

    @Pointcut("@annotation(tech.muyi.log.annotation.MyTraceIdCreate)")
    public void traceIdCreateAspect() {
    }
    @Around("traceIdCreateAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature sign = (MethodSignature) joinPoint.getSignature();
            Method method = sign.getMethod();
            //获取方法上的注解
            MyTraceIdCreate myTraceIdCreate = method.getAnnotation(MyTraceIdCreate.class);
            LogRecordContext.onceRestLogRecord(myTraceIdCreate);
            return joinPoint.proceed();
        }finally {
            LogRecordContext.clearMDC();
        }
    }
}
