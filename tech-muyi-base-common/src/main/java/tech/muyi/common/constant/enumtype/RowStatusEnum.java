package tech.muyi.common.constant.enumtype;

/**
 * 数据库逻辑行状态
 * @Author: muyi
 * @Date: 2021/1/3 21:12
 */
public enum RowStatusEnum {
    DELETE(-1),
    NORMAL(0);

    private Integer code;

    RowStatusEnum(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
