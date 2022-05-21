package tech.muyi.util.request;

import java.lang.annotation.*;

/**
 * 请求路径收集器
 * @Author: muyi
 * @Date: 2021/1/26 19:40
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestCollect {
    String value() default "";
}
