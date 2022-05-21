package tech.muyi.message.mail;

/**
 * description: ReceiveUserDTO
 * date: 2021/10/23 14:58
 * author: muyi
 * version: 1.0
 */
public class ReceiveUserDTO {
    /**
     * 接收邮箱
     */
    private String mail;
    /**
     * 接收地址
     */
    private String name;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
