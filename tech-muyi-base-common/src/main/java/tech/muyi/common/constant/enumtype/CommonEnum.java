package tech.muyi.common.constant.enumtype;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Objects;

/**
 * 通用枚举协议：为枚举提供稳定的“对外 code”与展示名 name。
 *
 * <p>设计目的：
 * <ul>
 *   <li>避免对外接口直接暴露 {@link Enum#name()}（容易因重命名/调整导致兼容性问题）。</li>
 *   <li>统一前后端的枚举传输结构，配合自定义反序列化器实现“多形态入参兼容”。</li>
 * </ul>
 *
 * <p>{@link JsonValue}：默认把枚举序列化成一个对象（{@link CommonEnumJson}），而不是枚举名或 ordinal。</p>
 *
 * @author: muyi
 * @date: 2023/2/28
 **/
public interface CommonEnum<T>{
    T getCode();

    String getName();

    @JsonValue
    default CommonEnumJson<T> deserialization() {
        // 统一枚举 JSON 形态：{ "code": ..., "name": ... }
        return new CommonEnumJson(getCode(), getName());
    }
}
