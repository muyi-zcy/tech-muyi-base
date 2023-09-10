package tech.muyi.core.config.web.jackson.core.bind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.constant.enumtype.CommonEnum;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.io.IOException;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
    private Class<?> target;

    @SuppressWarnings("all")
    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        if (jsonParser == null || StringUtils.isEmpty(jsonParser.getText())){
            return null;
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
                if (((CommonEnum<?>) enumConstant).getCode().toString().equals(indexString)
                        || enumConstant.name().equals(indexString)
                        || ((CommonEnum<?>) enumConstant).getName().equals(indexString)) {
                    return enumConstant;
                }
            }
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM);
        } catch (Throwable e) {
            log.error("EnumDeserializer error:", e);
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        }
    }
}
