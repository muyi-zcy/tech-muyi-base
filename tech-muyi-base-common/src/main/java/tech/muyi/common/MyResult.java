package tech.muyi.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import tech.muyi.common.query.MyBaseQuery;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.io.Serializable;

/**
 * 统一的接口响应包装。
 *
 * <p>约定：
 * <ul>
 *   <li>success 表示业务是否成功（与 HTTP 状态码解耦）。</li>
 *   <li>code/status/message 用于错误码体系与人类可读提示。</li>
 *   <li>query 用于在分页/列表查询时把“查询元信息”回传给前端（current/size/total/pageTotal 等）。</li>
 * </ul>
 *
 * <p>注意：fail(Throwable) 会将非 {@link MyException} 归类为 UNKNOWN_EXCEPTION，并尽量保留 message；
 * 如需避免泄露内部异常信息，建议在上层统一包装/脱敏。</p>
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:27
 */
@Data
public class MyResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Integer SUCCESS_STATUS = NumberUtils.INTEGER_ZERO;
    private static String SUCCESS_CODE = String.valueOf(SUCCESS_STATUS);
    private static Integer UNKNOWN_ERROR_STATUS = NumberUtils.createInteger(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode());

    private boolean success;
    private T data;
    private MyBaseQuery query;
    private String code;
    private Integer status;
    private String message;
    private String requestId;


    public static <T> MyResult<T> ok(T data, MyBaseQuery query) {
        MyResult<T> result = new MyResult<>();
        result.setData(data);
        result.setQuery(query);
        // 成功响应固定使用 0/“0” 作为 code/status，便于跨端统一判断。
        result.success = true;
        result.setCode(SUCCESS_CODE);
        result.setStatus(SUCCESS_STATUS);
        return result;
    }

    public static <T> MyResult<T> ok() {
        return ok(null);
    }

    public static <T> MyResult<T> ok(T data) {
        return ok(data, null);
    }

    public static <T> MyResult<T> fail(String errCode, String errMsg) {
        MyResult<T> result = new MyResult<>();
        result.success = false;
        result.setCode(errCode);
        result.setMessage(errMsg);
        // code 若可转数字则映射到 status；否则统一落 UNKNOWN，避免 status 为空影响前端/网关统计。
        if (NumberUtils.isCreatable(errCode)) {
            result.setStatus(NumberUtils.createInteger(errCode));
        } else {
            result.setStatus(UNKNOWN_ERROR_STATUS);
        }
        return result;
    }

    public static <T> MyResult<T> fail(Throwable throwable) {
        if (throwable instanceof MyException) {
            MyException myException = (MyException) throwable;
            return fail(myException.getErrorCode(), myException.getErrorMsg());
        }
        // 非业务异常统一归类为 UNKNOWN；调用方 message 可能为空，因此上层可补默认提示。
        return fail(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(), throwable.getMessage());
    }
}
