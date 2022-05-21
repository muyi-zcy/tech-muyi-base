package tech.muyi.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: MyTraceIdCreate
 * date: 2022/1/2 23:04
 * author: muyi
 * version: 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTraceIdCreate {
    String ip() default "localhost";
    String deviceType()  default "null";
}
