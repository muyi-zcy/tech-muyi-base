package tech.muyi.core.config.web.jackson.core.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class LongSerializer extends JsonSerializer<Long> {
    public static final LongSerializer INSTANCE = new LongSerializer();
    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(value.toString());
        }
    }
}
