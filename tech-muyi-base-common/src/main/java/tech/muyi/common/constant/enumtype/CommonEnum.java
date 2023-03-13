package tech.muyi.common.constant.enumtype;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author: muyi
 * @date: 2023/2/28
 **/
public interface CommonEnum<T> {
    T getCode();

    String getName();

    @JsonValue
    default CommonEnumJson<T> deserialization() {
        return new CommonEnumJson(getCode(), getName());
    }

    static <E extends CommonEnum> E getEnumByCode(Class<E> clazz, Object code) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(codeEnum -> Objects.equals(codeEnum.getCode(), code))
                .findFirst()
                .orElse(null);
    }

    static <E extends CommonEnum> E getEnumByName(Class<E> clazz, Object name) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(codeEnum -> Objects.equals(codeEnum.getName(), name))
                .findFirst()
                .orElse(null);
    }
}
