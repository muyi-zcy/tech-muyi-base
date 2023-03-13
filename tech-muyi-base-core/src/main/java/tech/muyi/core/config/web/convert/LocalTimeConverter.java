package tech.muyi.core.config.web.convert;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

public class LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        if (StrUtil.isEmpty(source)) {
            return null;
        }
        return LocalTime.parse(source);
    }
}
