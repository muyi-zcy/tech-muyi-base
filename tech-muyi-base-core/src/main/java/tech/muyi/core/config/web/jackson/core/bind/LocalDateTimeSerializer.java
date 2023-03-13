package tech.muyi.core.config.web.jackson.core.bind;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();
    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(LocalDateTimeUtil.formatNormal(value));
        }
    }
}
