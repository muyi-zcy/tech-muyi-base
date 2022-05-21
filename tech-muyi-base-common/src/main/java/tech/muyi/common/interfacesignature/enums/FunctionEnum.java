package tech.muyi.common.interfacesignature.enums;

/**
 * @author: muyi
 * @date: 2021-01-13 23:44
 */
public enum FunctionEnum {

    SELECT(1,"查询"),

    INSERT(2,"插入"),

    DELETE(3,"删除"),

    UPDTE(4,"更新"),

    OPERATE(5,"操作"),

    OTHER(6,"其他"),

    EXPORT(7,"导出"),

    IMPORT(8,"导入");


    private Integer code;
    private String desc;
    FunctionEnum(Integer code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDesc(Integer code){
        for(FunctionEnum functionEnum : FunctionEnum.values()){
            if(functionEnum.getCode().equals(code)){
                return functionEnum.desc;
            }
        }
        return "other";
    }
}
