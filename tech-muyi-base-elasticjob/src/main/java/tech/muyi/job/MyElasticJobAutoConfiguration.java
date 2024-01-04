package tech.muyi.job;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import tech.muyi.job.annotation.MyElasticJob;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: muyi
 * @Date: 2021/2/1 20:12
 */
@Slf4j
@Configuration
@ConditionalOnExpression("'${muyi.job.elasticJob.zk.serverLists}'.length() > 0")
public class MyElasticJobAutoConfiguration {
    @Value("${muyi.job.elasticJob.zk.serverLists}")
    private String serverList;
    @Value("${muyi.job.elasticJob.zk.namespace}")
    private String namespace;
    @Autowired
    private ApplicationContext applicationContext;

    public MyElasticJobAutoConfiguration() {
    }

    @PostConstruct
    public void initElasticJob() {
        if(StringUtils.isEmpty(serverList) || StringUtils.isEmpty(namespace)){
            log.info("job连接zk失败");
            return;
        }
        log.info("开始配置elastic-job");
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(this.serverList, this.namespace);
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(10000);
        ZookeeperRegistryCenter regCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        regCenter.init();
        this.initSimpleJob(regCenter);
        log.info("配置elastic-job结束");
    }

    private void initSimpleJob(ZookeeperRegistryCenter regCenter) {
        Map<String, SimpleJob> simpleJobMap = this.applicationContext.getBeansOfType(SimpleJob.class);

        for (Map.Entry<String, SimpleJob> entry : simpleJobMap.entrySet()) {
            SimpleJob simpleJob = entry.getValue();
            MyElasticJob myElasticJob = simpleJob.getClass().getAnnotation(MyElasticJob.class);
            if (null != myElasticJob) {
                SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(
                        JobCoreConfiguration.newBuilder(
                                        myElasticJob.jobName(),
                                        myElasticJob.cron(),
                                        myElasticJob.shardingTotalCount())
                                .shardingItemParameters(myElasticJob.shardingItemParameters())
                                .description(myElasticJob.description())
                                .jobParameter(myElasticJob.jobParameter())
                                .build(), simpleJob.getClass().getCanonicalName());
                LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(myElasticJob.overwrite()).disabled(myElasticJob.disabled()).build();
                String dataSourceRef = myElasticJob.dataSource();
                this.bulidElasticJob(dataSourceRef, simpleJob, liteJobConfiguration, regCenter);
            }
        }

    }

    private void bulidElasticJob(String dataSourceRef, ElasticJob elasticJob, LiteJobConfiguration liteJobConfiguration, ZookeeperRegistryCenter regCenter) {
        if (StringUtils.isNotBlank(dataSourceRef)) {
            if (!this.applicationContext.containsBean(dataSourceRef)) {
                throw new RuntimeException("not exist datasource [" + dataSourceRef + "] !");
            }

            DataSource dataSource = (DataSource)this.applicationContext.getBean(dataSourceRef);
            JobEventRdbConfiguration jobEventRdbConfiguration = new JobEventRdbConfiguration(dataSource);
            SpringJobScheduler jobScheduler = new SpringJobScheduler(elasticJob, regCenter, liteJobConfiguration, jobEventRdbConfiguration);
            jobScheduler.init();
        } else {
            SpringJobScheduler jobScheduler = new SpringJobScheduler(elasticJob, regCenter, liteJobConfiguration);
            jobScheduler.init();
        }
    }
}
