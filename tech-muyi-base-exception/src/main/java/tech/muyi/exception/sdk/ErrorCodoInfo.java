package tech.muyi.exception.sdk;

import lombok.Builder;
import lombok.Data;
import tech.muyi.exception.sdk.enums.AnnoTypeEnum;

import java.util.List;

/**
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