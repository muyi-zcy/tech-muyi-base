package tech.muyi.core.config.web;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@FunctionalInterface
public interface Jackson2ObjectMapperBuilderCustomizer {

    /**
     * Customize the JacksonObjectMapperBuilder.
     * @param jacksonObjectMapperBuilder the JacksonObjectMapperBuilder to customize
     */
    void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);

}