package tech.muyi.common.constant.enumtype;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonEnumJson<T> {
    private T code;

    private String name;

    public CommonEnumJson(T code, String name) {
        this.code = code;
        this.name = name;
    }
}
