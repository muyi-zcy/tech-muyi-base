package tech.muyi.core.config.web.jackson.core.bind;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
    public static final DateSerializer INSTANCE = new DateSerializer();
    @Override
    public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(DateUtil.formatDateTime(value));
        }
    }
}
