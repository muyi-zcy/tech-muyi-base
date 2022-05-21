package tech.muyi.message.mail;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: muyi
 * @Date: 2021/1/12 0:01
 */
@Data
public class MailEntity implements Serializable {
    //发件人
    private String name;
    //收件地址
    private ReceiveUserInfo toMail;
    private List<ReceiveUserInfo> toMailList;
    //抄送地址
    private ReceiveUserInfo ccMail;
    private List<ReceiveUserInfo> ccMailList;

    //密送地址
    private ReceiveUserInfo bccMail;
    private List<ReceiveUserInfo> bccMailList;

    //主题
    private String theme;
    //内容
    private String content;
    //发送时间
    private Date sendTime;
}
