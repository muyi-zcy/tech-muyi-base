package tech.muyi.common.constant.enumtype;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Data;
import lombok.Getter;

/**
 * 数据库逻辑行状态
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:12
 */
@Getter
public enum RowStatusEnum implements CommonEnum<Integer> {
    DELETE(-1, "删除"),
    NORMAL(0, "正常");

    @EnumValue
    private Integer code;

    private String name;

    RowStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
