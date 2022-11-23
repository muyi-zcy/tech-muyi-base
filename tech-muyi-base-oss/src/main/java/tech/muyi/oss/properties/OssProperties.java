package tech.muyi.oss.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(
        prefix = "muyi.oss"
)
@Configuration
public class OssProperties {

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 端口号
     */
    private Integer port;
    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;
    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    private Boolean secure;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }
}
