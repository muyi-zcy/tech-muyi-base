package tech.muyi.core.config.web.convert;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EnumConvertFactory<T extends Enum> implements ConverterFactory<String, T> {
    private static final Map<String, Field> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap();

    @Override
    public <E extends T> Converter<String, E> getConverter(Class<E> targetType) {
        return new StringToIEnum<>(targetType);
    }

    private static class StringToIEnum<T extends Enum> implements Converter<String, T> {
        private Class<T> targerType;

        public StringToIEnum(Class<T> targerType) {
            this.targerType = targerType;
        }

        @Override
        public T convert(String source) {
            if (StrUtil.isEmpty(source)) {
                return null;
            }
            return (T) EnumConvertFactory.getEnum(this.targerType, source);
        }
    }

    public static <T extends Enum> Object getEnum(Class<T> targerType, String source) {
        Field field = (Field) findEnumValueFieldName(targerType)
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", targerType))
                );

        for (T enumObj : targerType.getEnumConstants()) {
            if (source.equals(String.valueOf(ReflectUtil.getFieldValue(enumObj, field)))) {
                return enumObj;
            }
        }
        return null;
    }

    public static Optional<Field> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            Field field = TABLE_METHOD_OF_ENUM_TYPES.get(className);
            if (field != null) {
                return Optional.of(field);
            }
            return Optional.ofNullable(TABLE_METHOD_OF_ENUM_TYPES.computeIfAbsent(className, (key) ->
                    Arrays.stream(clazz.getDeclaredFields())
                            .filter(item -> item.isAnnotationPresent(EnumValue.class))
                            .findFirst()
                            .orElse(null)
            ));
        } else {
            return Optional.empty();
        }
    }

}
