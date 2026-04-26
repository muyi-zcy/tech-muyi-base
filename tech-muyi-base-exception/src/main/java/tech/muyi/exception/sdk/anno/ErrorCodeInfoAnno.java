package tech.muyi.exception.sdk.anno;

import java.lang.annotation.*;

/**
 * 错误码分组声明注解。
 *
 * <p>用于标记在错误码枚举类上，提供分组名称、分组 code、父分组和描述信息，
 * 供运行时扫描构建错误码目录树。</p>
 *
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
