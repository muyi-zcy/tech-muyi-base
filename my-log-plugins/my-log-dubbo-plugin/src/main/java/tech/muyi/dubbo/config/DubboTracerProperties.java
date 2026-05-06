package tech.muyi.dubbo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dubbo Tracer 配置属性
 *
 * @author muyi
 */
@ConfigurationProperties(prefix = "tech.muyi.tracer.dubbo")
public class DubboTracerProperties {

    /**
     * 是否启用 Dubbo 追踪
     */
    private boolean enabled = true;

    /**
     * 是否记录请求参数
     */
    private boolean logParameters = true;

    /**
     * 是否记录响应结果
     */
    private boolean logResult = false;

    /**
     * 参数最大长度
     */
    private int maxParameterLength = 1024;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogParameters() {
        return logParameters;
    }

    public void setLogParameters(boolean logParameters) {
        this.logParameters = logParameters;
    }

    public boolean isLogResult() {
        return logResult;
    }

    public void setLogResult(boolean logResult) {
        this.logResult = logResult;
    }

    public int getMaxParameterLength() {
        return maxParameterLength;
    }

    public void setMaxParameterLength(int maxParameterLength) {
        this.maxParameterLength = maxParameterLength;
    }
}
