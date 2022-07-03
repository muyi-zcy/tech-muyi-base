package tech.muyi.exception.enumtype;


import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;

/**
 * 公共错误码
 * @Author: muyi
 * @Date: 2021/1/3 21:38
 */
@ErrorCodeInfoAnno(name = "基础公共错误码", code = "base-error-code", desc = "基础公共错误码")
public enum CommonErrorCodeEnum implements BaseErrorInfoInterface {
    /***************************************************************************************************************
     *                通用状态码:
     *                1XX	信息，服务器收到请求，需要请求者继续执行操作
     *                2XX	成功，操作被成功接收并处理
     *                3XX	重定向，需要进一步的操作以完成请求
     *                4XX	客户端错误，请求包含语法错误或无法完成请求
     *                5XX	服务器错误，服务器在处理请求的过程中发生了错误
     *                6XX   未知错误
     *                后续各中间件和服务的异常由各自复制，按照规范分配
     *                1000：系统启动和运行异常
     *                2000：网关服务(包含api和rpc)
     *                3000：关系型数据库
     *                4000：非关系型数据库
     *                5000：定时任务
     *                6000：文件存储
     *                7000：mq
     *                8000：配置中心
     *                100000：分配给各业务服务
     * *************************************************************************************************************/

    CONTINUE("100", "Continue"),
    SWITCHING_PROTOCOLS("101", "Switching Protocols"),
    PROCESSING("102", "Processing"),
    CHECKPOINT("103", "Checkpoint"),

    OK("200", "OK"),
    CREATED("201", "Created"),
    ACCEPTED("202", "Accepted"),
    NON_AUTHORITATIVE_INFORMATION("203", "Non-Authoritative Information"),
    NO_CONTENT("204", "No Content"),
    RESET_CONTENT("205", "Reset Content"),
    PARTIAL_CONTENT("206", "Partial Content"),
    MULTI_STATUS("207", "Multi-Status"),
    ALREADY_REPORTED("208", "Already Reported"),
    IM_USED("226", "IM Used"),

    MULTIPLE_CHOICES("300", "Multiple Choices"),
    MOVED_PERMANENTLY("301", "Moved Permanently"),
    FOUND("302", "Found"),
    MOVED_TEMPORARILY("302", "Moved Temporarily"),
    SEE_OTHER("303", "See Other"),
    NOT_MODIFIED("304", "Not Modified"),
    USE_PROXY("305", "Use Proxy"),
    TEMPORARY_REDIRECT("307", "Temporary Redirect"),
    PERMANENT_REDIRECT("308", "Permanent Redirect"),

    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),
    PAYMENT_REQUIRED("402", "Payment Required"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Not Found"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed"),
    NOT_ACCEPTABLE("406", "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED("407", "Proxy Authentication Required"),
    REQUEST_TIMEOUT("408", "Request Timeout"),
    CONFLICT("409", "Conflict"),
    GONE("410", "Gone"),
    LENGTH_REQUIRED("411", "Length Required"),
    PRECONDITION_FAILED("412", "Precondition Failed"),
    PAYLOAD_TOO_LARGE("413", "Payload Too Large"),
    REQUEST_ENTITY_TOO_LARGE("413", "Request Entity Too Large"),
    URI_TOO_LONG("414", "URI Too Long"),
    REQUEST_URI_TOO_LONG("414", "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE("415", "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE("416", "Requested range not satisfiable"),
    EXPECTATION_FAILED("417", "Expectation Failed"),
    I_AM_A_TEAPOT("418", "I'm a teapot"),
    INSUFFICIENT_SPACE_ON_RESOURCE("419", "Insufficient Space On Resource"),
    METHOD_FAILURE("420", "Method Failure"),
    DESTINATION_LOCKED("421", "Destination Locked"),
    UNPROCESSABLE_ENTITY("422", "Unprocessable Entity"),
    LOCKED("423", "Locked"),
    FAILED_DEPENDENCY("424", "Failed Dependency"),
    TOO_EARLY("425", "Too Early"),
    UPGRADE_REQUIRED("426", "Upgrade Required"),
    PRECONDITION_REQUIRED("428", "Precondition Required"),
    TOO_MANY_REQUESTS("429", "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE("431", "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS("451", "Unavailable For Legal Reasons"),

    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    NOT_IMPLEMENTED("501", "Not Implemented"),
    BAD_GATEWAY("502", "Bad Gateway"),
    SERVICE_UNAVAILABLE("503", "Service Unavailable"),
    GATEWAY_TIMEOUT("504", "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED("505", "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES("506", "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE("507", "Insufficient Storage"),
    LOOP_DETECTED("508", "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED("509", "Bandwidth Limit Exceeded"),
    NOT_EXTENDED("510", "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED("511", "Network Authentication Required"),


    UNKNOWN_EXCEPTION( "600", "未知异常"),
    SERVER_BUSY("611","服务器限流"),



    INVALID_PARAM( "10000", "参数不合法"),
    DB_EXCEPTION("10001", "数据库异常"),
    NULL_POINTER("10002", "空指针异常"),
    FLOW_EXCEPTION("10003", "限流"),
    SERIALIZATION_FAIL("10004","序列化失败"),
    ;

    /** 错误码 */
    private String resultCode;

    /** 错误描述 */
    private String resultMsg;

    CommonErrorCodeEnum(String resultCode, String resultMsg) {
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
