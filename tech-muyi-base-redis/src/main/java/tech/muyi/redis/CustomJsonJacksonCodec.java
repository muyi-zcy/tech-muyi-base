package tech.muyi.redis;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.redisson.codec.JsonJacksonCodec;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Redisson JSON 编解码器扩展。
 *
 * <p>为 {@link LocalDateTime} 显式注册 ISO-8601 序列化/反序列化规则，
 * 避免默认配置在跨服务通信时出现格式不一致。</p>
 */
public class CustomJsonJacksonCodec extends JsonJacksonCodec {

    public CustomJsonJacksonCodec() {
        super(new ObjectMapper());
        configureObjectMapper();
    }

    private void configureObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        SimpleModule module = new SimpleModule();

        // 自定义 LocalDateTime 序列化
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });

        // 自定义 LocalDateTime 反序列化
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });

        // 仅追加模块，不覆盖 Redisson/Jackson 的其他默认能力。
        objectMapper.registerModule(module);
    }
}