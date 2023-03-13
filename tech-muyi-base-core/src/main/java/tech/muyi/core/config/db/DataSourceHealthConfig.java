package tech.muyi.core.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: muyi
 * @date: 2022/7/10
 **/
@Configuration
public class DataSourceHealthConfig {

    /**
     * 解决新版Spring中,集成sharding jdbc 健康检查异常
     */
    @Bean
    DataSourcePoolMetadataProvider dataSourcePoolMetadataProvider() {
        return dataSource -> dataSource instanceof HikariDataSource
                ? new HikariDataSourcePoolMetadata((HikariDataSource) dataSource)
                : new NotAvailableDataSourcePoolMetadata();
    }

    /**
     * 不可用的数据源池元数据.
     */
    private static class NotAvailableDataSourcePoolMetadata implements DataSourcePoolMetadata {
        @Override
        public Float getUsage() {
            return null;
        }

        @Override
        public Integer getActive() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public String getValidationQuery() {
            return "select 1";
        }

        @Override
        public Boolean getDefaultAutoCommit() {
            return null;
        }
    }
}