package tech.muyi.db.constant.enumtype;

/**
 * @author: muyi
 * @date: 2022/10/31
 **/
public enum RowStatusEnum {

    NO_DELETE("未删除",1),
    YES_DELETE("已删除",0);
    private Integer value;
    private String code;

    RowStatusEnum(String code, Integer value){
        this.value = value;
        this.code = code;
    }
}