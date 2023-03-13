package tech.muyi.common.constant.enumtype;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * description: DeviceEnum
 * date: 2021/11/13 22:09
 * author: muyi
 * version: 1.0
 */
@Getter
public enum DeviceEnum implements CommonEnum<Integer>{
    MOBILE(1,"MOBILE"),
    TABLET(2,"TABLET"),
    NORMAL(3,"NORMAL"),
    OTHER(4,"OTHER");

    @EnumValue
    private Integer code;

    private String name;

    DeviceEnum(int code,String name) {
        this.code = code;
        this.name = name;
    }

}
