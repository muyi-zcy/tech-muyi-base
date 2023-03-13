package tech.muyi.oss.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(
        prefix = "muyi.file.ftp"
)
@Configuration
@Data
public class FtpProperties {

    private boolean enable  = false;
    private String endpoint;
    private Integer port;
    private String name;
    private String password;
    // 访问前缀
    private String urlPrefix;
    private String path = "";
    private boolean passiveMode = false;
    private String encoding = "UTF-8";
    private int connectTimeout = 30000;
    private int maxActive = 1;
    private int bufferSize = 8096;
    private boolean lazyActive = false;
    // 申请连接时直接检测连接是否有效,开启会降低性能
    private boolean testOnBorrow = true;
    // 归还连接时检测连接是否有效,开启会降低性能
    private boolean testOnReturn = false;
    // 是否对操作对象缓存及缓存对象数量
    private boolean poolCatch = false;
    private int poolCatchSize = 5;
    // 是否做存活检测
    private boolean keepAlive = false;
    // 存活检测间隔时间
    private int keepAliveCheckTime = 60000;
}
