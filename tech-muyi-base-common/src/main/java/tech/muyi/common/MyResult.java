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
    private String errCode;
    private String errMsg;

    public MyResult() {
    }


    public static <T> MyResult ok(T data, MyBaseQuery query) {
        MyResult<T> result = new MyResult();
        result.setData(data);
        result.setQuery(query);
        result.isSuccess();
        result.setErrCode("0");
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
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
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

    public String getErrCode() {
        return this.errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
