package tech.muyi.exception;

/**
 * 错误码协议接口。
 *
 * <p>所有对外暴露的错误码枚举都应实现该接口，确保：
 * <ul>
 *   <li>code 作为机器可读、稳定不变的契约字段</li>
 *   <li>msg 作为人类可读描述，可按需要迭代文案</li>
 * </ul>
 * </p>
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:27
 */
public interface BaseErrorInfoInterface {
    /** 错误码*/
    String getResultCode();

    /** 错误描述*/
    String getResultMsg();
}
