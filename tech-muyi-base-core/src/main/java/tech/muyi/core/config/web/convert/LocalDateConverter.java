package tech.muyi.core.config.web.convert;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

public class LocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        if (StrUtil.isEmpty(source)) {
            return null;
        }
        return LocalDateTimeUtil.parseDate(source);
    }
}
