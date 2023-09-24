package tech.muyi.core.config.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.core.config.web.jackson.core.bind.*;
import tech.muyi.util.MyJson;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class JacksonConfig  {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ObjectMapper objectMapper() {

        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }

        SimpleModule simpleModule = new SimpleModule();
        simpleModule
                .addDeserializer(Enum.class, new EnumDeserializer())
                .addSerializer(BigDecimal.class, BigDecimalSerializer.INSTANCE)
                .addSerializer(Long.class, LongSerializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE)
                .addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);

        objectMapper.registerModule(simpleModule);

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MyJson.init(objectMapper);
        return objectMapper;
    }
}
