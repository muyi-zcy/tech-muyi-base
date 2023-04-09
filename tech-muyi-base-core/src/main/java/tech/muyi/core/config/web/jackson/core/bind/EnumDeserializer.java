package tech.muyi.core.config.web.jackson.core.bind;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import tech.muyi.common.constant.enumtype.CommonEnum;
import tech.muyi.core.config.web.convert.EnumConvertFactory;

import java.io.IOException;
import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
    private Class<?> target;

    @SuppressWarnings("all")
    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        if (!StringUtils.hasText(jsonParser.getText())) {
            return null;
        }
        if (IEnum.class.isAssignableFrom(target)) {
            return (Enum<?>) EnumConvertFactory.getEnum((Class) target, jsonParser.getText());
        }
        return defaultEnumTransform(target, jsonParser.getText());
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) {
        Class<?> rawCls = ctx.getContextualType().getRawClass();
        EnumDeserializer enumDeserializer = new EnumDeserializer();
        enumDeserializer.setTarget(rawCls);
        return enumDeserializer;
    }


    public static Enum<?> defaultEnumTransform(Class<?> type, String indexString) {
        Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
        try {
            for (Enum<?> enumConstant : enumConstants) {
                if (!(enumConstant instanceof CommonEnum)) {
                    continue;
                }
                if (enumConstant.name().equals(indexString)
                        || ((CommonEnum<?>) enumConstant).getName().equals(indexString)
                        || ((CommonEnum<?>) enumConstant).getCode().toString().equals(indexString)) {
                    return enumConstant;
                }
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
