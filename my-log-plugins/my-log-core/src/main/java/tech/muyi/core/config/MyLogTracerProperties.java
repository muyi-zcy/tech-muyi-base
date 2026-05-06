package tech.muyi.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyLog Tracer 配置属性
 *
 * @author muyi
 */
@ConfigurationProperties(prefix = "tech.muyi.tracer")
public class MyLogTracerProperties {

    /**
     * 是否启用链路追踪
     */
    private boolean enabled = true;

    /**
     * 日志输出路径
     */
    private String logPath = "./logs";

    /**
     * 日志保留天数
     */
    private int logReserveDays = 7;

    /**
     * 采样率（0.0 - 1.0）
     */
    private double samplingRate = 1.0;

    /**
     * 是否记录方法参数
     */
    private boolean logParameters = true;

    /**
     * 参数最大长度
     */
    private int maxParameterLength = 1000;

    /**
     * 是否启用异步日志输出
     */
    private boolean asyncLog = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getLogReserveDays() {
        return logReserveDays;
    }

    public void setLogReserveDays(int logReserveDays) {
        this.logReserveDays = logReserveDays;
    }

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public boolean isLogParameters() {
        return logParameters;
    }

    public void setLogParameters(boolean logParameters) {
        this.logParameters = logParameters;
    }

    public int getMaxParameterLength() {
        return maxParameterLength;
    }

    public void setMaxParameterLength(int maxParameterLength) {
        this.maxParameterLength = maxParameterLength;
    }

    public boolean isAsyncLog() {
        return asyncLog;
    }

    public void setAsyncLog(boolean asyncLog) {
        this.asyncLog = asyncLog;
    }
}
