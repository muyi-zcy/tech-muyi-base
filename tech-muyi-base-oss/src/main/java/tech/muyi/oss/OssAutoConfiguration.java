package tech.muyi.oss;

import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import tech.muyi.exception.MyException;
import tech.muyi.oss.exception.OssErrorCodeEnum;
import tech.muyi.oss.properties.OssProperties;


/**
 * @author: muyi
 * @date: 2022/5/2
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoConfiguration {
    @Autowired
    private OssProperties ossProperties;

    public OssAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioClient sntOssTemplate(){
        MinioClient minioClient = null;
        if(ossProperties == null || StringUtils.isEmpty(ossProperties.getEndpoint()) || StringUtils.isEmpty(ossProperties.getAccessKey())|| StringUtils.isEmpty(ossProperties.getSecretKey())){
            return minioClient;
        }
        try {
            minioClient = new MinioClient(
                    ossProperties.getEndpoint(),
                    ossProperties.getPort(),
                    ossProperties.getAccessKey(),
                    ossProperties.getSecretKey());

        } catch (InvalidEndpointException | InvalidPortException e) {
            log.error("OSS连接服务器失败，错误原因：{}",e.getMessage());
            throw new MyException(OssErrorCodeEnum.CONN_ERROR,e);
        }
        return minioClient;
    }
}
