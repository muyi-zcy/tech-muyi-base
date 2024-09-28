package tech.muyi.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import tech.muyi.common.query.MyBaseQuery;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.io.Serializable;

/**
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
        return fail(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(), throwable.getMessage());
    }
}
