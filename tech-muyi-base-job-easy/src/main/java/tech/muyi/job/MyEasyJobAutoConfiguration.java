package tech.muyi.job;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import tech.muyi.job.annotation.MyEasyJob;
import tech.muyi.job.base.BaseTask;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: muyi
 * @Date: 2021/2/1 20:12
 */
@Slf4j
@Configuration
@ConditionalOnExpression
public class MyEasyJobAutoConfiguration {
    @Autowired
    private ApplicationContext applicationContext;

    public MyEasyJobAutoConfiguration() {
        Map<String, BaseTask> baseTaskMap = this.applicationContext.getBeansOfType(BaseTask.class);
        Iterator iterator = baseTaskMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, BaseTask> entry = (Map.Entry)iterator.next();
            BaseTask baseTask = (BaseTask)entry.getValue();
            MyEasyJob myEasyJob = (MyEasyJob)baseTask.getClass().getAnnotation(MyEasyJob.class);

            if(null != myEasyJob){
                String cron = myEasyJob.cron();
                String name = myEasyJob.jobName();
                this.buildTask(cron,baseTask,name);
            }
        }
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    public void buildTask(String cron, BaseTask baseTask,String name){
        CronUtil.schedule(cron, new Task() {
            @Override
            public void execute() {
                baseTask.run();
            }
        });
    }
}
