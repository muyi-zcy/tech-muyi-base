package tech.muyi.core.config.web.jackson.core.bind;

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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> implements ContextualSerializer {
    public static final BigDecimalSerializer INSTANCE = new BigDecimalSerializer();

    private DecimalFormat decimalFormat;

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            if(decimalFormat != null){
                jsonGenerator.writeString(decimalFormat.format(value));
            }else {
                jsonGenerator.writeString(value.toPlainString());
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), BigDecimal.class)) {
                JsonFormat jsonFormat = beanProperty.getAnnotation(JsonFormat.class);
                if (jsonFormat != null) {
                    DecimalFormat decimalFormat = new DecimalFormat(jsonFormat.pattern());
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    return new BigDecimalSerializer(decimalFormat);
                }
            }
        }
        return this;
    }
}
