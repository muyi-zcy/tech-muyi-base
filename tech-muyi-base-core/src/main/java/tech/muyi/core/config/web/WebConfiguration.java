package tech.muyi.core.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.muyi.core.config.web.convert.EnumConvertFactory;
import tech.muyi.core.config.web.convert.LocalDateConverter;
import tech.muyi.core.config.web.convert.LocalDateTimeConverter;
import tech.muyi.core.config.web.convert.LocalTimeConverter;

/**
 * @author liangzhihao
 * 分页插件
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConvertFactory());
        registry.addConverter(new LocalDateTimeConverter());
        registry.addConverter(new LocalDateConverter());
        registry.addConverter(new LocalTimeConverter());
    }
}
