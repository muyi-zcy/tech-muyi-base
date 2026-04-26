package tech.muyi.core.config.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 分页插件配置。
 *
 * <p>说明：
 * <ul>
 *   <li>{@link PaginationInnerInterceptor} 会对带分页参数的查询进行物理分页改写（LIMIT/OFFSET 等）。</li>
 *   <li>此处显式指定 {@link DbType#MYSQL}，避免自动推断失败导致分页 SQL 不匹配。</li>
 * </ul>
 *
 * @author: muyi
 * @date: 2022/10/12
 **/
@Slf4j
@Configuration
public class PageConfig {
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 将分页拦截器加入到 MP 拦截链中；一个应用通常只需要一个 MybatisPlusInterceptor Bean。
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
