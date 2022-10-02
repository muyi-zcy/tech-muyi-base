package tech.muyi.oss.exception;

import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;

/**
 * @author: muyi
 * @date: 2022/6/26
 **/

@ErrorCodeInfoAnno(
        name = "文件存储错误码",
        parentCode = "base-error-code",
        code = "oss-error-code",
        desc = "文件存储错误码-base")
public enum OssErrorCodeEnum implements BaseErrorInfoInterface {
    CONN_ERROR("6011", "Oss服务器连接失败"),
    EXIST_BUCKET("6020", "令牌桶不存在"),
    EXIST_BUCKET_ERROR("6021", "判断令牌桶是否存在失败"),

    MAKE_BUCKET_ERROR("6022", "创建存储桶失败"),
    LIST_BUCKET_ERROR("6023", "查询存储桶列表失败"),
    REMOVE_BUCKET_ERROR("6024", "删除存储桶失败"),
    LIST_OBJECTS_ERROR("6025", "查询存储桶下文件列表失败"),
    PUT_OBJECTS_ERROR("6026", "上传文件失败"),
    STAT_OBJECTS_ERROR("6027", "获取文件元数据失败"),
    PRESIGNED_GET_OBJECTS_ERROR("6028", "生成URL失败"),
    ;

    /**
     * 错误码
     */
    private String resultCode;

    /**
     * 错误描述
     */
    private String resultMsg;

    OssErrorCodeEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
