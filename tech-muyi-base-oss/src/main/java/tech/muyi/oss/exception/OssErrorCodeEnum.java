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


    FILE_NOT_FOUND("6011", "文件不存在"),
    FOLDER_NOT_FOUND("6012", "文件夹不存在"),
    FOLDER_MKDIR_ERROR("6013", "文件夹创建失败"),
    FILE_DELETE_ERROR("6014", "文件删除失败"),
    UPLOAD_FILE_ERROR("6215", "上传文件失败"),

    OSS_CONN_ERROR("6211", "Oss服务器连接失败"),
    OSS_EXIST_BUCKET("6220", "令牌桶不存在"),
    OSS_EXIST_BUCKET_ERROR("6221", "判断令牌桶是否存在失败"),

    OSS_MAKE_BUCKET_ERROR("6222", "创建存储桶失败"),
    OSS_LIST_BUCKET_ERROR("6223", "查询存储桶列表失败"),
    OSS_REMOVE_BUCKET_ERROR("6224", "删除存储桶失败"),
    OSS_LIST_OBJECTS_ERROR("6225", "查询存储桶下文件列表失败"),
    OSS_PUT_OBJECTS_ERROR("6226", "上传文件失败"),
    OSS_STAT_OBJECTS_ERROR("6227", "获取文件元数据失败"),
    OSS_PRESIGNED_GET_OBJECTS_ERROR("6228", "生成URL失败"),
    OSS_OBJECT_DOWNLOAD_ERROR("6229", "文件下载失败"),
    OSS_OBJECT_DELETE_ERROR("6230", "文件删除失败"),
    FTP_CONN_ERROR("6310", "FTP服务器连接失败"),
    FTP_LOGIN_ERROR("6311", "FTP服务器登录失败"),
    FTP_LOGOUT_ERROR("6312", "FTP服务器登出失败"),
    FTP_DISCONN_ERROR("6312", "FTP服务器断开连接失败"),
    FTP_CHANGE_DIR_ERROR("6313", "FTP切换目录失败"),
    FTP_PWD_ERROR("6314", "FTP获取当前目录失败"),
    FTP_LS_ERROR("6315", "FTP展示当前目录下文件失败"),
    FTP_MKDIR_ERROR("6316", "FTP创建文件夹失败"),
    FTP_DEL_ERROR("6317", "FTP删除文件失败"),
    FTP_UPLOAD_ERROR("6318", "FTP上传文件失败"),
    FTP_DOWNLOAD_ERROR("6319", "FTP下载文件失败"),
    FTP_BINARY_FILE_TYPE_ERROR("6320", "FTP指定文件传输类型失败"),

    
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
