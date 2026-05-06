package tech.muyi.db.config;

import com.alipay.sofa.tracer.plugins.datasource.SmartDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tech.muyi.core.config.MyLogTracerProperties;

import javax.sql.DataSource;

/**
 * DataSource Tracer 自动配置
 * 包装官方 sofa-tracer-datasource-plugin，提供统一配置
 *
 * @author muyi
 */
@Configuration
@EnableConfigurationProperties({DataSourceTracerProperties.class, MyLogTracerProperties.class})
@ConditionalOnProperty(prefix = "tech.muyi.tracer.db", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceTracerAutoConfiguration {

    private final DataSourceTracerProperties properties;
    private final MyLogTracerProperties myLogTracerProperties;
    private final Environment environment;

    public DataSourceTracerAutoConfiguration(DataSourceTracerProperties properties,
                                            MyLogTracerProperties myLogTracerProperties,
                                            Environment environment) {
        this.properties = properties;
        this.myLogTracerProperties = myLogTracerProperties;
        this.environment = environment;

        // 设置日志路径
        System.setProperty("logging.path", myLogTracerProperties.getLogPath());
        // 设置系统属性
        if (properties.isLogParameters()) {
            System.setProperty("com.alipay.sofa.tracer.datasource.log.parameters", "true");
        }
        if (properties.getMaxParameterLength() > 0) {
            System.setProperty("com.alipay.sofa.tracer.datasource.max.parameter.length",
                    String.valueOf(properties.getMaxParameterLength()));
        }
        if (properties.isLogResultSetSize()) {
            System.setProperty("com.alipay.sofa.tracer.datasource.log.resultset.size", "true");
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "tech.muyi.tracer.db", name = "wrapDataSource", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor dataSourceWrapperBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DataSource && !(bean instanceof SmartDataSource)) {
                    SmartDataSource smartDataSource = new SmartDataSource((DataSource) bean);
                    // 设置必要的属性
                    String[] dbInfo = detectDbInfo((DataSource) bean);
                    smartDataSource.setDbType(dbInfo[0]);
                    smartDataSource.setDatabase(dbInfo[1]);
                    smartDataSource.setAppName(getAppName());
                    // 初始化 SmartDataSource
                    smartDataSource.init();
                    return smartDataSource;
                }
                return bean;
            }
        };
    }

    /**
     * 检测数据库类型和数据库名
     * @return [dbType, database]
     */
    private String[] detectDbInfo(DataSource dataSource) {
        String dbType = "unknown";
        String database = "unknown";
        try {
            String url = dataSource.getConnection().getMetaData().getURL();
            if (url.contains("mysql")) {
                dbType = "mysql";
                database = extractDatabaseFromUrl(url, "mysql");
            } else if (url.contains("postgresql")) {
                dbType = "postgresql";
                database = extractDatabaseFromUrl(url, "postgresql");
            } else if (url.contains("oracle")) {
                dbType = "oracle";
                database = extractDatabaseFromUrl(url, "oracle");
            } else if (url.contains("sqlserver")) {
                dbType = "sqlserver";
                database = extractDatabaseFromUrl(url, "sqlserver");
            } else if (url.contains("h2")) {
                dbType = "h2";
                database = extractDatabaseFromUrl(url, "h2");
            }
        } catch (Exception e) {
            // ignore
        }
        return new String[]{dbType, database};
    }

    /**
     * 从 JDBC URL 中提取数据库名
     */
    private String extractDatabaseFromUrl(String url, String dbType) {
        try {
            // jdbc:mysql://localhost:3306/dbname?params
            // jdbc:postgresql://localhost:5432/dbname?params
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash > 0) {
                String dbPart = url.substring(lastSlash + 1);
                int questionMark = dbPart.indexOf('?');
                if (questionMark > 0) {
                    return dbPart.substring(0, questionMark);
                }
                return dbPart;
            }
        } catch (Exception e) {
            // ignore
        }
        return "unknown";
    }

    /**
     * 获取应用名称
     */
    private String getAppName() {
        // 优先从 Spring Environment 获取
        String appName = environment.getProperty("spring.application.name");
        if (appName == null || appName.isEmpty()) {
            appName = System.getProperty("spring.application.name");
        }
        if (appName == null || appName.isEmpty()) {
            appName = System.getenv("SPRING_APPLICATION_NAME");
        }
        if (appName == null || appName.isEmpty()) {
            appName = "unknown-app";
        }
        return appName;
    }

}
