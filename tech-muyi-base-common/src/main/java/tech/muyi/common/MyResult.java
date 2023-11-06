package tech.muyi.common;

import lombok.Data;
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

    private boolean success;
    private T data;
    private MyBaseQuery query;
    private String code;
    private String message;


    public static <T> MyResult<T> ok(T data, MyBaseQuery query) {
        MyResult<T> result = new MyResult<>();
        result.setData(data);
        result.setQuery(query);
        result.success = true;
        result.setCode("0");
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
