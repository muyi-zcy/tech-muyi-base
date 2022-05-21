package tech.muyi.common.constant.enumtype;

/**
 * description: DeviceEnum
 * date: 2021/11/13 22:09
 * author: muyi
 * version: 1.0
 */
public enum DeviceEnum {
    MOBILE(1,"MOBILE"),
    TABLET(2,"TABLET"),
    NORMAL(3,"NORMAL"),
    OTHER(4,"OTHER");

    private Integer code;

    private String desc;

    private DeviceEnum(int code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.code;
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
        for(DeviceEnum item : DeviceEnum.values()){
            if(item.getCode() == code){
                return item.desc;
            }
        }
        return "other";
    }
}
