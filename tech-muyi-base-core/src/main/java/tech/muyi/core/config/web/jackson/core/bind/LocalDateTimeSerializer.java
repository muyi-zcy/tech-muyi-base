package tech.muyi.core.config.web.jackson.core.bind;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {
    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    private JsonFormat format;


    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            if (format == null) {
                jsonGenerator.writeString(LocalDateTimeUtil.formatNormal(value));
            } else {
                jsonGenerator.writeString(LocalDateTimeUtil.format(value, format.pattern()));
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), LocalDateTime.class)) {
                JsonFormat jsonFormat = beanProperty.getAnnotation(JsonFormat.class);
                if (jsonFormat != null) {
                    return new LocalDateTimeSerializer(format);
                }
            }
        }
        return this;
    }
}
