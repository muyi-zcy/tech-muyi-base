package tech.muyi.message.mail;

import lombok.Data;

import java.io.Serializable;

/**
 * description: ReceiveUserDTO
 * date: 2021/10/23 14:58
 * author: muyi
 * version: 1.0
 */
@Data
public class ReceiveUserInfo implements Serializable {
    /**
     * 接收邮箱
     */
    private String mail;
    /**
     * 接收地址
     */
    private String name;

}
