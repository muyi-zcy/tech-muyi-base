package tech.muyi.common.DTO.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.util.ApplicationContextUtil;

import java.io.IOException;
import java.util.Objects;

public class OperatorSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private String extraFieldName = null;

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
            if (StringUtils.isNotEmpty(extraFieldName)) {
                gen.writeObject(value);
                gen.writeStringField(extraFieldName, operator);
            } else {
                gen.writeObject(operator);
            }
        } catch (Exception e) {
            try {
                gen.writeObject(value);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return prov.findNullValueSerializer(null);
        }
        Operator operator = property.getAnnotation(Operator.class);
        if (operator == null) {
            operator = property.getContextAnnotation(Operator.class);
        }
        if (operator != null) {
            this.extraFieldName = operator.extraFieldName();
        }
        return this;
    }
}
