package tech.muyi.core.config.web.jackson.core.bind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
public class EnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
    private Class<?> target;

    @SuppressWarnings("all")
    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        if(jsonParser == null){
            return null;
        }
        String value = null;
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node instanceof ObjectNode){
            value = node.get("code").asText();
        }else if (node != null) {
            value = node.asText();
        }
        if (StringUtils.isEmpty(value)){
            return null;
        }
        return defaultEnumTransform(target,value);
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
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        } catch (Throwable e) {
            log.error("EnumDeserializer error:", e);
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        }
    }
}
