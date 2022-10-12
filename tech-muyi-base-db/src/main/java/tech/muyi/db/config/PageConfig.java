package tech.muyi.db.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: muyi
 * @date: 2022/10/12
 **/
@Slf4j
@Configuration
public class PageConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        log.info("加载分页插件：success......");
        return new PaginationInterceptor();
    }
}
