package tech.muyi.core.config.web.jackson.core.bind;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {
    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    private String format;

    public LocalDateTimeSerializer() {

    }

    public LocalDateTimeSerializer(String format) {
        this.format = format;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(LocalDateTimeUtil.formatNormal(value));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) { // 为空直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), LocalDateTime.class)) {
                JsonFormat jsonFormat = beanProperty.getAnnotation(JsonFormat.class);
                if (jsonFormat != null) {
                    return new LocalDateTimeSerializer(jsonFormat.pattern());
                }
            }
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}
