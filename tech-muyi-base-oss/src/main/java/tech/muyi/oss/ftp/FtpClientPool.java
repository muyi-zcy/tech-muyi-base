package tech.muyi.oss.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.BaseObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tech.muyi.oss.properties.FtpProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author: muyi
 * @date: 2022/11/29
 **/
@Slf4j
@Configuration
@ConditionalOnClass(FTPClient.class)
@AutoConfigureBefore(FtpClientFactory.class)
@EnableConfigurationProperties({FtpProperties.class})
@ConditionalOnProperty(name = {"muyi.file.ftp.enable"}, havingValue = "true")
@Import({FtpClientFactory.class})
public class FtpClientPool extends BaseObjectPool<FTPClient> {

    private BlockingQueue<FTPClient> ftpBlockingQueue;

    @Autowired
    private  FtpClientFactory ftpClientFactory;

    @Autowired
    private FtpProperties ftpProperties;

    @PostConstruct
    public void init() throws Exception {
        ftpBlockingQueue = new ArrayBlockingQueue<>(ftpProperties.getMaxActive());
        if(!ftpProperties.isLazyActive()) {
            // 懒加载
            for (int i = 0; i < ftpProperties.getMaxActive(); i++) {
                addObject();
            }
        }
    }

    @Override
    public FTPClient borrowObject() throws Exception {
        FTPClient client = ftpBlockingQueue.poll();
        if (ObjectUtils.isEmpty(client)) {
            client = ftpClientFactory.create();
        } else if (ftpProperties.isTestOnBorrow() && !ftpClientFactory.validateObject(ftpClientFactory.wrap(client))) {
            invalidateObject(client);
            client = ftpClientFactory.create();
        }
        return client;
    }

    @Override
    public void returnObject(FTPClient client) {
        if (client != null && !ftpBlockingQueue.offer(client)) {
            ftpClientFactory.destroyObject(ftpClientFactory.wrap(client));
        }
    }

    @Override
        public void invalidateObject(FTPClient client) {
        try {
            if (client.isConnected()) {
                client.logout();
            }
        } catch (IOException io) {
            log.error("校验ftp客户端不可用！", io);
        } finally {
            try {
                client.disconnect();
            } catch (IOException io) {
                log.error("登出ftp客户端异常！", io);
            }
            ftpBlockingQueue.remove(client);
        }
    }

    @Override
    public void addObject() throws Exception {
        // 插入对象到队列
        if(ftpBlockingQueue.offer(ftpClientFactory.create())){
            log.error("添加新的ftp客户端异常！");
        }
    }

    /**
     * 关闭连接池
     */
    @Override
    public void close() {
        try {
            while (ftpBlockingQueue.iterator().hasNext()) {
                FTPClient client = ftpBlockingQueue.take();
                ftpClientFactory.destroyObject(ftpClientFactory.wrap(client));
            }
        } catch (Exception e) {
            log.error("关闭ftp客户端异常！", e);
        }
    }

    public BlockingQueue<FTPClient> getFtpBlockingQueue() {
        return ftpBlockingQueue;
    }
}