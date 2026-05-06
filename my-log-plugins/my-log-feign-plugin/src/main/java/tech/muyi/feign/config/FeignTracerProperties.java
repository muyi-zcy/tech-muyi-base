package tech.muyi.feign.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Feign Tracer 配置属性
 *
 * @author muyi
 */
@ConfigurationProperties(prefix = "tech.muyi.tracer.feign")
public class FeignTracerProperties {

    /**
     * 是否启用 Feign 追踪
     */
    private boolean enabled = true;

    /**
     * 是否记录请求体
     */
    private boolean logRequestBody = false;

    /**
     * 是否记录响应体
     */
    private boolean logResponseBody = false;

    /**
     * 请求体最大长度
     */
    private int maxRequestBodyLength = 1024;

    /**
     * 响应体最大长度
     */
    private int maxResponseBodyLength = 1024;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogRequestBody() {
        return logRequestBody;
    }

    public void setLogRequestBody(boolean logRequestBody) {
        this.logRequestBody = logRequestBody;
    }

    public boolean isLogResponseBody() {
        return logResponseBody;
    }

    public void setLogResponseBody(boolean logResponseBody) {
        this.logResponseBody = logResponseBody;
    }

    public int getMaxRequestBodyLength() {
        return maxRequestBodyLength;
    }

    public void setMaxRequestBodyLength(int maxRequestBodyLength) {
        this.maxRequestBodyLength = maxRequestBodyLength;
    }

    public int getMaxResponseBodyLength() {
        return maxResponseBodyLength;
    }

    public void setMaxResponseBodyLength(int maxResponseBodyLength) {
        this.maxResponseBodyLength = maxResponseBodyLength;
    }
}
