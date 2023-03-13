package tech.muyi.core.config.web.jackson.core.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
    public static final BigDecimalSerializer INSTANCE = new BigDecimalSerializer();

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(value.setScale(2, RoundingMode.HALF_DOWN) + "");
        }
    }
}
