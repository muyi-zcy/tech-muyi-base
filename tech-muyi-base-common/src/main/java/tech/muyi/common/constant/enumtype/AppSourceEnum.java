package tech.muyi.common.constant.enumtype;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 数据来源
 * @Author: muyi
 * @Date: 2021/1/3 21:13
 */
@Getter
public enum AppSourceEnum implements CommonEnum<Integer>{
    APP(1,"APP"),
    APPLET(2,"APPLET"),
    WEB(3,"WEB"),
    LOT(4,"LOT"),
    PDA(5,"PDA"),
    SCO(6,"SCO"),
    PC(7,"PC"),
    POS(8,"POS"),
    OTHER(9,"OTHER");

    @EnumValue
    private Integer code;

    private String name;

    AppSourceEnum(int code,String name) {
        this.code = code;
        this.name = name;
    }
}
