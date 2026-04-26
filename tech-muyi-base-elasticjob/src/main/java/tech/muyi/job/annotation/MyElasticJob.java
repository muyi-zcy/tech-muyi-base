package tech.muyi.job.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ElasticJob 任务声明注解。
 *
 * <p>描述分片、cron、参数、数据源与启停覆盖策略，用于自动注册 ElasticJob。</p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyElasticJob {
    String id();

    String cron() default "";

    String jobName() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";

    String jobParameter() default "";

    String dataSource() default "";

    String description() default "";

    boolean disabled() default true;

    boolean overwrite() default false;
}
