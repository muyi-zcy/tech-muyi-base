package tech.muyi.job;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
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
                scheduledTaskRegistrar.addTriggerTask(() -> process(baseTask), triggerContext -> new CronTrigger(easyJob.cron()).nextExecutionTime(triggerContext));
            }else {
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

        return cronSequenceGenerator.next(new Date()).getTime() - (nextNextNextTime.getTime() - nextNextTime.getTime());
    }
}
