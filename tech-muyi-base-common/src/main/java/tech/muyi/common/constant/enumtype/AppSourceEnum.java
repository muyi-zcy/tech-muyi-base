package tech.muyi.common.constant.enumtype;

/**
 * 数据来源
 * @Author: muyi
 * @Date: 2021/1/3 21:13
 */
public enum AppSourceEnum {
    APP(1,"APP"),
    WX_APPLET(2,"WXAPPLET"),
    WEB(3,"WEB"),
    LOT(4,"LOT"),
    PDA(5,"PDA"),
    SCO(6,"SCO"),
    PC(7,"PC"),
    POS(8,"POS"),
    OTHER(9,"OTHER");

    private Integer code;

    private String desc;

    private AppSourceEnum(int code,String desc) {
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
        for(AppSourceEnum item : AppSourceEnum.values()){
            if(item.getCode() == code){
                return item.desc;
            }
        }
        return "other";
    }
}
