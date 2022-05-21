package tech.muyi.log.annotation;

import java.lang.annotation.*;

/**
 * description: LogRecordAnnotation
 * 参数名	    描述	                        是否必填
 * success	    操作日志的文本模板	            是
 * fail	        操作日志失败的文本版本	        否
 * operator	    操作日志的执行人	            否
 * bizNo	    操作日志绑定的业务对象标识	    是
 * category	    操作日志的种类	                否
 * detail	    扩展参数，记录操作日志的修改详情	否
 * condition	记录日志的条件	                否
 * date: 2021/11/13 20:47
 * author: muyi
 * version: 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogRecord {
    /**
     * 操作日志的文本模板
     * @return
     */
    String success();

    /**
     * 操作日失败的文本模板
     * @return
     */
    String fail() default "";

    /**
     * 操作日志的执行人
     * @return
     */
    String operator() default "";

    String prefix();

    String bizNo();

    /**
     * 操作日志的种类
     * @return
     */
    String category() default "";

    /**
     * 扩展参数, 记录操作日志的修改详情
     * @return
     */
    String detail() default "";

    /**
     * 记录日志的条件
     * @return
     */
    String condition() default "";
}