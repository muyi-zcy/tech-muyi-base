package tech.muyi.job.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EasyJob 任务元数据注解。
 *
 * <p>声明任务 ID、cron 与展示名，供 `MyEasyJobAutoConfiguration` 扫描注册。</p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyEasyJob {
    String id();

    String cron();

    String jobName();

}
