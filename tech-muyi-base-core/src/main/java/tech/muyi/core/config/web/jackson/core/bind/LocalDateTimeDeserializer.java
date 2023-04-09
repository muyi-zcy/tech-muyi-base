package tech.muyi.core.config.web.jackson.core.bind;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(p.getValueAsString());
            return localDateTime;
        }catch (Exception e){
            throw new MyException(CommonErrorCodeEnum.DESERIALIZATION_FAIL);
        }
    }
}