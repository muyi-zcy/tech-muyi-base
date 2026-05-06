package tech.muyi.db.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tech.muyi.tracer.db")
public class DataSourceTracerProperties {

    private boolean enabled = true;
    /**
     * 是否将 DataSource 包装为 SOFA {@code SmartDataSource}（仅依赖插件时须开启，否则仅有系统属性不会采集 JDBC）
     */
    private boolean wrapDataSource = true;
    private boolean logParameters = true;
    private int maxParameterLength = 1024;
    private boolean logResultSetSize = false;

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

    public int getMaxParameterLength() {
        return maxParameterLength;
    }

    public void setMaxParameterLength(int maxParameterLength) {
        this.maxParameterLength = maxParameterLength;
    }

    public boolean isLogResultSetSize() {
        return logResultSetSize;
    }

    public void setLogResultSetSize(boolean logResultSetSize) {
        this.logResultSetSize = logResultSetSize;
    }

    public boolean isWrapDataSource() {
        return wrapDataSource;
    }

    public void setWrapDataSource(boolean wrapDataSource) {
        this.wrapDataSource = wrapDataSource;
    }
}
