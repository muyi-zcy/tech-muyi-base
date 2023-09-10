package tech.muyi.exception.sdk.anno;

import java.lang.annotation.*;

/**
 * @author: muyi
 * @date: 2022/6/12
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ErrorCodeInfoAnno {
    /**
     * 状态服务名称
     * @return
     */
    String name();

    /**
     * code
     * @return
     */
    String code();

    /**
     * 上级服务
     * @return
     */
    String parentCode() default "0";

    /**
     * 描述
     * @return
     */
    String desc();
}
