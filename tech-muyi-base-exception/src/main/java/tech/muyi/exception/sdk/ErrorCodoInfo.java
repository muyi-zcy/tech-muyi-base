package tech.muyi.exception.sdk;

import lombok.Builder;
import lombok.Data;
import tech.muyi.exception.sdk.enums.AnnoTypeEnum;

import java.util.List;

/**
 * 错误码树节点 DTO。
 *
 * <p>用于承载错误码目录的层级结构：
 * TYPE 节点可包含 childInfo，CODE 节点通常为叶子节点。</p>
 *
 * @author: muyi
 * @date: 2022/6/13
 **/
@Data
@Builder
public class ErrorCodoInfo {
    private String name;
    private String code;
    private String desc;
    private String parentCode;
    private AnnoTypeEnum typeEnum;
    private List<ErrorCodoInfo> childInfo;
}