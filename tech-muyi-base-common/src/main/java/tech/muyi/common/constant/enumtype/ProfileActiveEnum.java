package tech.muyi.common.constant.enumtype;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;

import java.util.Objects;

/**
 * description: 系统环境枚举
 * date: 2022/1/3 0:15
 * author: muyi
 * version: 1.0
 */
@Getter
public enum ProfileActiveEnum implements CommonEnum<String> {
    LOCAL("local","本地环境"),
    DEV("dev","开发环境"),
    PRE("pre","预发环境"),
    PROD("prod","生产环境");

    @EnumValue
    private String code;

    private String name;

    ProfileActiveEnum(String code,String name) {
        this.code = code;
        this.name = name;
    }
}
