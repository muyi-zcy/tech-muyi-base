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
}
