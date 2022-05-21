package tech.muyi.common.interfacesignature.annotation;

import tech.muyi.common.constant.enumtype.AppSourceEnum;
import tech.muyi.common.interfacesignature.enums.FunctionEnum;

import java.lang.annotation.*;

/**
 *
 * @author: muyi
 * @date: 2021-01-13 23:13
 */

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterfaceSignature {
    /**
     * module
     * @return
     */
    String module() default "other";

    FunctionEnum function() default FunctionEnum.OTHER;

    AppSourceEnum appSource() default AppSourceEnum.OTHER ;
}
