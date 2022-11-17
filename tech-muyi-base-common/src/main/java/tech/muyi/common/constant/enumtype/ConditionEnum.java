package tech.muyi.common.constant.enumtype;


import org.apache.commons.lang3.StringUtils;

/**
 * description: ConditionEnum
 * date: 2022/1/9 17:56
 * author: muyi
 * version: 1.0
 */
public enum ConditionEnum  {
    AND("且","AND","AND"),
    OR("或","OR","OR"),
    IN("包含","IN","IN"),
    NOT("否","NOT","NOT"),
    LIKE("模糊匹配","LIKE","LIKE"),
    NOT_LIKE("反向模糊匹配","NOT_LIKE","NOT_LIKE"),
    EQ("等于","EQ","="),
    NE("不等于","NE","<>"),
    GT("大于","",">"),
    GE("大于等于","GE",">="),
    LT("小于","LT","<"),
    LE("小于等于","LE","<="),
    IS_NULL("为空","IS_NULL","IS NULL"),
    IS_NOT_NULL("不为空","IS_NOT_NULL","IS NOT NULL"),
    BETWEEN("介于","BETWEEN","BETWEEN"),
    NOT_BETWEEN("不介于","NOT_BETWEEN","NOT_BETWEEN"),
    ASC("正序","ASC","ASC"),
    DESC("倒序","DESC","DESC");

    private String alias;
    private String code;
    private String condition;

    ConditionEnum(String alias, String code, String condition) {
        this.alias = alias;
        this.code = code;
        this.condition = condition;
    }

    public static ConditionEnum getConditionByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (ConditionEnum result : ConditionEnum.values()) {
            if (result.code.equals(code) || result.condition.equals(code)) {
                return result;
            }
        }
        return null;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
