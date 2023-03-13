package tech.muyi.oss.alive;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tech.muyi.oss.ftp.FtpClientFactory;
import tech.muyi.oss.ftp.FtpClientPool;
import tech.muyi.oss.properties.FtpProperties;

import javax.annotation.PostConstruct;
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
public class ClientKeepAlive {

    private FtpKeepAlive ftpKeepAlive;

    @Autowired(required = false)
    private FtpClientPool ftpClientPool;


    @Autowired
    private FtpProperties ftpProperties;


    @PostConstruct
    public void init() {
        // 启动心跳检测线程
        if (ftpKeepAlive == null && ftpProperties != null && ftpProperties.isKeepAlive()) {
            ftpKeepAlive = new FtpKeepAlive();
            String THREAD_NAME = "oss-client-alive-thread";
            Thread thread = new Thread(ftpKeepAlive, THREAD_NAME);
            thread.start();
        }
    }

    class FtpKeepAlive implements Runnable {
        @Override
        public void run() {
            FTPClient ftpClient = null;
            while (true) {
                try {
                    BlockingQueue<FTPClient> pool = ftpClientPool.getFtpBlockingQueue();
                    if (pool != null && pool.size() > 0) {
                        for (FTPClient client : pool) {
                            ftpClient = client;
                            boolean result = ftpClient.sendNoOp();
                            log.debug("心跳结果: {}", result);
                            if (!result) {
                                ftpClientPool.invalidateObject(ftpClient);
                            }
                        }

                    }
                } catch (Exception e) {
                    log.error("ftp心跳检测异常", e);
                    ftpClientPool.invalidateObject(ftpClient);
                }
                try {
                    Thread.sleep(ftpProperties.getKeepAliveCheckTime());
                } catch (InterruptedException e) {
                    log.error("休眠异常", e);
                }
            }
        }
    }
}