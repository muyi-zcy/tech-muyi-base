package tech.muyi.springmvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SpringMVC Tracer 配置属性
 *
 * @author muyi
 */
@ConfigurationProperties(prefix = "tech.muyi.tracer.springmvc")
public class SpringMvcTracerProperties {

    /**
     * 是否启用 SpringMVC 追踪
     */
    private boolean enabled = true;

    /**
     * Filter 顺序
     */
    private int filterOrder = -2147483647;

    /**
     * URL 匹配模式
     */
    private String urlPatterns = "/*";

    /**
     * 是否记录请求参数
     */
    private boolean logParameters = true;

    /**
     * 是否记录响应体
     */
    private boolean logResponseBody = false;

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

    public int getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public String getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public boolean isLogParameters() {
        return logParameters;
    }

    public void setLogParameters(boolean logParameters) {
        this.logParameters = logParameters;
    }

    public boolean isLogResponseBody() {
        return logResponseBody;
    }

    public void setLogResponseBody(boolean logResponseBody) {
        this.logResponseBody = logResponseBody;
    }

    public int getMaxResponseBodyLength() {
        return maxResponseBodyLength;
    }

    public void setMaxResponseBodyLength(int maxResponseBodyLength) {
        this.maxResponseBodyLength = maxResponseBodyLength;
    }
}
