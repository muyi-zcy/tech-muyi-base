package tech.muyi.common.DTO.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.muyi.util.ApplicationContextUtil;

import java.io.IOException;

public class OperatorSerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeObject(value);
            return;
        }
        try {
            IOperatorHandler iOperatorHandler = ApplicationContextUtil.getBean(IOperatorHandler.class);
            if (iOperatorHandler == null) {
                gen.writeObject(value);
                return;
            }
            String operator = iOperatorHandler.convertOperator(value);
            gen.writeObject(operator);
        } catch (Exception e) {
            try {
                gen.writeObject(value);
            } catch (Throwable ignored) {
            }
        }
    }
}
