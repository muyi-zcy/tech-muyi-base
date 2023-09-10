package tech.muyi.common;


import tech.muyi.common.query.MyBaseQuery;
import tech.muyi.exception.MyException;

import java.io.Serializable;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:27
 */
public class MyResult<T> implements Serializable {
    private static final long serialVersionUID = 2994494589314183496L;
    private boolean success;
    private T data;
    private MyBaseQuery query;
    private String code;
    private String message;

    public MyResult() {
    }


    public static <T> MyResult ok(T data, MyBaseQuery query) {
        MyResult<T> result = new MyResult();
        result.setData(data);
        result.setQuery(query);
        result.isSuccess();
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
        MyResult result = new MyResult();
        result.isSuccess();
        result.setCode(errCode);
        result.setMessage(errMsg);
        return result;
    }

    public static <T> MyResult<T> fail(MyException e) {
        return fail(e.getErrorCode(), e.getErrorMsg());
    }

    public boolean isSuccess() {
        return this.success;
    }


    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public MyBaseQuery getQuery() {
        return this.query;
    }

    public void setQuery(MyBaseQuery query) {
        this.query = query;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
