package tech.muyi.message.mail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: muyi
 * @Date: 2021/1/12 0:01
 */
public class MailDTO implements Serializable {
    //发件人
    private String name;
    //收件地址
    private ReceiveUserDTO toMail;
    private List<ReceiveUserDTO> toMailList;
    //抄送地址
    private ReceiveUserDTO ccMail;
    private List<ReceiveUserDTO> ccMailList;

    //密送地址
    private ReceiveUserDTO bccMail;
    private List<ReceiveUserDTO> bccMailList;

    //主题
    private String theme;
    //内容
    private String content;
    //发送时间
    private Date sendTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReceiveUserDTO getToMail() {
        return toMail;
    }

    public void setToMail(ReceiveUserDTO toMail) {
        this.toMail = toMail;
    }

    public List<ReceiveUserDTO> getToMailList() {
        return toMailList;
    }

    public void setToMailList(List<ReceiveUserDTO> toMailList) {
        this.toMailList = toMailList;
    }

    public ReceiveUserDTO getCcMail() {
        return ccMail;
    }

    public void setCcMail(ReceiveUserDTO ccMail) {
        this.ccMail = ccMail;
    }

    public List<ReceiveUserDTO> getCcMailList() {
        return ccMailList;
    }

    public void setCcMailList(List<ReceiveUserDTO> ccMailList) {
        this.ccMailList = ccMailList;
    }

    public ReceiveUserDTO getBccMail() {
        return bccMail;
    }

    public void setBccMail(ReceiveUserDTO bccMail) {
        this.bccMail = bccMail;
    }

    public List<ReceiveUserDTO> getBccMailList() {
        return bccMailList;
    }

    public void setBccMailList(List<ReceiveUserDTO> bccMailList) {
        this.bccMailList = bccMailList;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
