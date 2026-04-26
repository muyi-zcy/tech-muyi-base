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
    /**
     * 目标枚举类型（由 Jackson 在 createContextual 阶段根据字段/泛型上下文设置）。
     *
     * <p>原因：同一个反序列化器会应用于不同字段的 Enum 类型，必须在上下文化时“绑定具体枚举类”。</p>
     */
    private Class<?> target;

    @SuppressWarnings("all")
    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        // Jackson 正常情况下不会传 null；这里做兜底避免极端情况下 NPE。
        if (jsonParser == null) {
            return null;
        }
        String value = null;
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // 入参兼容两种形态：
        // 1) 对象：{ "code": "xxx", ... } —— 常见于前端下拉回传整个对象
        // 2) 标量："xxx"/123 —— 常见于只传 code 或枚举名
        if (node instanceof ObjectNode) {
            value = node.get("code").asText();
        } else if (node != null) {
            value = node.asText();
        }
        // 空值按“未传/不覆盖”处理，避免把空串硬映射成某个枚举导致业务歧义。
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        // 将输入字符串映射到目标枚举常量；失败会抛业务异常（统一错误码）。
        return defaultEnumTransform(target, value);
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) {
        // 通过上下文化拿到具体字段的原始枚举类型，从而让同一反序列化器适配不同 Enum 字段。
        Class<?> rawCls = ctx.getContextualType().getRawClass();
        EnumDeserializer enumDeserializer = new EnumDeserializer();
        enumDeserializer.setTarget(rawCls);
        return enumDeserializer;
    }


    public static Enum<?> defaultEnumTransform(Class<?> type, String indexString) {
        // 仅支持实现了 CommonEnum 的枚举（需要提供 code/name 语义）；其它普通枚举默认不做映射。
        Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
        try {
            for (Enum<?> enumConstant : enumConstants) {
                if (!(enumConstant instanceof CommonEnum)) {
                    continue;
                }
                // 兼容多种匹配策略：
                // - code：用于稳定的协议值（推荐）
                // - enum name：便于快速联调/后端直传
                // - name(业务名)：便于某些老接口直接传中文/展示值
                if (((CommonEnum<?>) enumConstant).getCode().toString().equals(indexString)
                        || enumConstant.name().equals(indexString)
                        || ((CommonEnum<?>) enumConstant).getName().equals(indexString)) {
                    return enumConstant;
                }
            }
            // 未匹配到任何常量：抛出统一错误码，便于全局异常处理返回稳定响应。
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        } catch (Throwable e) {
            // 这里捕获 Throwable 是为了兜住枚举实现异常（例如 getCode/getName NPE）并统一报反序列化失败，
            // 避免把底层异常细节直接暴露给调用方。
            log.error("EnumDeserializer error:", e);
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        }
    }
}
