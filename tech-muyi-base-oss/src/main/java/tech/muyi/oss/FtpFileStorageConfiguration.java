package tech.muyi.oss;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import tech.muyi.oss.condition.FtpStorageEnabledCondition;
import tech.muyi.oss.properties.FtpProperties;

/**
 * 当 {@code muyi.file.storage-type=ftp} 时注册 {@link FileStorageTemplate}，委托 {@link FtpTemplate}。
 */
@Configuration
@ConditionalOnProperty(name = "muyi.file.storage-type", havingValue = "ftp")
@ConditionalOnClass(FTPClient.class)
@Conditional(FtpStorageEnabledCondition.class)
@AutoConfigureAfter(FtpTemplate.class)
public class FtpFileStorageConfiguration {

    @Bean(name = "fileStorageTemplate")
    public FtpDelegatingFileStorage fileStorageTemplate(FtpTemplate ftpTemplate, FtpProperties ftpProperties) {
        return new FtpDelegatingFileStorage(ftpTemplate, ftpProperties);
    }
}
