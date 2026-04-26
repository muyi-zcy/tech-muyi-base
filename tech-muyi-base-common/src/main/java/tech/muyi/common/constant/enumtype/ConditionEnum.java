package tech.muyi.common.constant.enumtype;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * description: ConditionEnum
 *
 * <p>查询条件操作符枚举：
 * <ul>
 *   <li>code：用于协议值（以及 MyBatis-Plus 的 {@link EnumValue} 持久化/映射）</li>
 *   <li>operator：用于某些场景下与实际 SQL 运算符/关键字对齐</li>
 * </ul>
 *
 * <p>本枚举会在 static 块中注册到 {@link EnumCache}，支持按 name/value 快速反查，供查询条件解析使用。</p>
 *
 * date: 2022/1/9 17:56
 * author: muyi
 * version: 1.0
 */
@Getter
public enum ConditionEnum implements CommonEnum<String> {
    AND("且", "AND", "AND"),
    OR("或", "OR", "OR"),
    IN("包含", "IN", "IN"),
    NOT("否", "NOT", "NOT"),
    LIKE("模糊匹配", "LIKE", "LIKE"),
    NOT_LIKE("反向模糊匹配", "NOT_LIKE", "NOT_LIKE"),
    EQ("等于", "EQ", "="),
    NE("不等于", "NE", "<>"),
    GT("大于", "", ">"),
    GE("大于等于", "GE", ">="),
    LT("小于", "LT", "<"),
    LE("小于等于", "LE", "<="),
    IS_NULL("为空", "IS_NULL", "IS NULL"),
    IS_NOT_NULL("不为空", "IS_NOT_NULL", "IS NOT NULL"),
    BETWEEN("介于", "BETWEEN", "BETWEEN"),
    NOT_BETWEEN("不介于", "NOT_BETWEEN", "NOT_BETWEEN"),
    ASC("正序", "ASC", "ASC"),
    DESC("倒序", "DESC", "DESC");

    private String name;
    @EnumValue
    private String code;
    private String operator;

    ConditionEnum(String name, String code, String operator) {
        this.name = name;
        this.code = code;
        this.operator = operator;
    }

    static {
        // 预热缓存：提升运行期反查性能，减少每次遍历 values() 的开销。
        EnumCache.registerByName(ConditionEnum.class, ConditionEnum.values());
        EnumCache.registerByValue(ConditionEnum.class, ConditionEnum.values(), ConditionEnum::getCode);
    }


    public static ConditionEnum findCondition(String condition) {
        // 先按 code 反查（最稳定的协议值），找不到再尝试 operator 兼容。
        ConditionEnum result = EnumCache.findByValue(ConditionEnum.class, condition.toUpperCase());
        if (result != null) {
            return result;
        }
        for (ConditionEnum conditionEnum : values()) {
            if (conditionEnum.getOperator().equals(condition)) {
                return conditionEnum;
            }
        }
        return null;
    }
}
