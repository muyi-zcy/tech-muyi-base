package tech.muyi.core.config.web.jackson.core.bind;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> implements ContextualDeserializer {
    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private JsonFormat format;

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            if (format == null) {
                return LocalDateTime.parse(p.getValueAsString(), formatter);
            } else {
                return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern(format.pattern()));
            }
        } catch (Exception e) {
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        }
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), LocalDateTime.class)) {
                JsonFormat jsonFormat = beanProperty.getAnnotation(JsonFormat.class);
                if (jsonFormat != null) {
                    return new LocalDateTimeDeserializer(format);
                }
            }
        }
        return this;
    }
}