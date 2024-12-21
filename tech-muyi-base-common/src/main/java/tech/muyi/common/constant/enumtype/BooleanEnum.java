package tech.muyi.common.constant.enumtype;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;


@Getter
public enum BooleanEnum implements CommonEnum<Integer> {
    TRUE(1, "是"),

    FALSE(0, "否");

    @EnumValue
    private Integer code;

    private String name;

    BooleanEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    static {
        EnumCache.registerByName(BooleanEnum.class, BooleanEnum.values());
        EnumCache.registerByValue(BooleanEnum.class, BooleanEnum.values(), BooleanEnum::getCode);
    }
}
