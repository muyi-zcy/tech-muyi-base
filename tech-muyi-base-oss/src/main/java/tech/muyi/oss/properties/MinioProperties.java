package tech.muyi.oss.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 连接配置。
 *
 * <p>绑定 `muyi.file.minio`，提供 endpoint、鉴权与 OkHttp 连接池参数。</p>
 */
@ConfigurationProperties(
        prefix = "muyi.file.minio"
)
@Configuration
@Data
public class MinioProperties {
    private boolean enable = false;
    private String endpoint;
    private Integer port;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private long connectTimeout = 15 * 60;
    private int maxActive = 1;
    //  每个连接存活时间,单位分钟
    private long keepAliveDuration = 5;
}
