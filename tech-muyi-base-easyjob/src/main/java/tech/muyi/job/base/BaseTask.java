package tech.muyi.job.base;

/**
 * EasyJob 任务抽象。
 *
 * <p>业务任务实现该接口后，可由自动配置按注解信息动态注册到调度器。</p>
 */
public interface BaseTask {
    void run();
}
