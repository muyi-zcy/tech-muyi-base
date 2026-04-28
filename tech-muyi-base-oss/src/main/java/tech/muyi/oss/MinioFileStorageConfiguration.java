package tech.muyi.oss;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import tech.muyi.oss.condition.MinioStorageEnabledCondition;
import tech.muyi.oss.properties.MinioProperties;

/**
 * 当 {@code muyi.file.storage-type=minio} 时注册 {@link FileStorageTemplate}，委托 {@link MinioTemplate}。
 */
@Configuration
@ConditionalOnProperty(name = "muyi.file.storage-type", havingValue = "minio")
@ConditionalOnClass(MinioClient.class)
@Conditional(MinioStorageEnabledCondition.class)
@AutoConfigureAfter(MinioTemplate.class)
public class MinioFileStorageConfiguration {

    @Bean(name = "fileStorageTemplate")
    public MinioDelegatingFileStorage fileStorageTemplate(MinioTemplate minioTemplate, MinioProperties minioProperties) {
        return new MinioDelegatingFileStorage(minioTemplate, minioProperties);
    }
}
