package tech.muyi.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import tech.muyi.job.annotation.MyEasyJob;
import tech.muyi.job.base.BaseTask;
import tech.muyi.redis.RedissonManage;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * 轻量任务调度自动配置。
 *
 * <p>扫描 `BaseTask + @MyEasyJob` 并注册 cron 触发任务；若存在 Redisson，则启用分布式互斥执行。</p>
 *
 * @Author: muyi
 * @Date: 2021/2/1 20:12
 */
@Slf4j
@Configuration
@EnableScheduling
public class MyEasyJobAutoConfiguration implements SchedulingConfigurer {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RedissonManage redissonManage;

    public void buildTask(ScheduledTaskRegistrar scheduledTaskRegistrar, BaseTask baseTask) {
        MyEasyJob easyJob = baseTask.getClass().getAnnotation(MyEasyJob.class);
        if (easyJob.cron() != null) {
            log.info("添加定时任务：{},id:{},corn：{}", easyJob.jobName(), easyJob.id(), easyJob.cron());
            if(redissonManage == null){
                // 单机模式：不加分布式锁，按本地调度执行。
                scheduledTaskRegistrar.addTriggerTask(() -> process(baseTask), triggerContext -> new CronTrigger(easyJob.cron()).nextExecutionTime(triggerContext));
            }else {
                // 集群模式：通过 Redis 锁 + once 记录防止重复执行。
                scheduledTaskRegistrar.addTriggerTask(() -> lockProcess(baseTask), triggerContext -> new CronTrigger(easyJob.cron()).nextExecutionTime(triggerContext));
            }
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        Map<String, BaseTask> baseTaskMap = this.applicationContext.getBeansOfType(BaseTask.class);
        Iterator iterator = baseTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BaseTask> entry = (Map.Entry) iterator.next();
            BaseTask baseTask = entry.getValue();
            if (null != baseTask.getClass().getAnnotation(MyEasyJob.class)) {
                    this.buildTask(scheduledTaskRegistrar, baseTask);
            }
        }
    }

    public void process(BaseTask baseTask) {
        MyEasyJob easyJob = baseTask.getClass().getAnnotation(MyEasyJob.class);
        Long thisTaskTime = getThisTaskTime(easyJob.cron());
        log.info("[{}]当前节点开始执行定时任务,开始时间：[{}]", easyJob.jobName(), thisTaskTime);
        baseTask.run();
        log.info("[{}]当前节点执行定时任务完成", easyJob.jobName());
    }

    public void lockProcess(BaseTask baseTask) {
        MyEasyJob easyJob = baseTask.getClass().getAnnotation(MyEasyJob.class);

        Long thisTaskTime = getThisTaskTime(easyJob.cron());

        String lockKey = redissonManage.getKey(easyJob.id());
        try {

            String onceKey = redissonManage.getKey(easyJob.id() + ":once");
            if (redissonManage.tryLock(lockKey)) {
                Long lastTaskTime = redissonManage.get(onceKey);
                if (lastTaskTime != null) {
                    if (lastTaskTime >= thisTaskTime) {
                        // redis记录上次执行的时间比本次任务执行的时间完或相等
                        log.info("[{}]当前节点此次任务[{}]已被执行，当前节点放弃，等待下次执行", easyJob.jobName(), thisTaskTime);
                        return;
                    }
                }
                log.info("[{}]当前节点开始执行定时任务,开始时间：[{}]", easyJob.jobName(), thisTaskTime);
                baseTask.run();
                redissonManage.set(onceKey, thisTaskTime);
                log.info("[{}]当前节点执行定时任务完成", easyJob.jobName());
            } else {
                log.info("[{}]已有节点开始执行定时任务，当前节点放弃，等待下次执行", easyJob.jobName());
            }
        } finally {
            // 释放锁
            redissonManage.unlock(lockKey);
            log.info("[{}]当前节点释放任务！！", easyJob.jobName());
        }
    }

    private Long getThisTaskTime(String corn) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(corn);
        Date nextTime = cronSequenceGenerator.next(new Date());
        Date nextNextTime = cronSequenceGenerator.next(nextTime);
        Date nextNextNextTime = cronSequenceGenerator.next(nextNextTime);

        // 通过周期反推“当前应执行时间点”，用于跨节点幂等判断。
        return cronSequenceGenerator.next(new Date()).getTime() - (nextNextNextTime.getTime() - nextNextTime.getTime());
    }
}
