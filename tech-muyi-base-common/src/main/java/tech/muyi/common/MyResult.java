package tech.muyi.common;

import tech.muyi.common.query.BaseQuery;
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
    private BaseQuery query;
    private String errCode;
    private String errMsg;

    public MyResult() {
    }


    public static <T> MyResult ok(T data, BaseQuery query) {
        MyResult<T> result = new MyResult();
        result.setData(data);
        result.setQuery(query);
        result.setSuccess(true);
        result.setErrCode("0");
        return result;
    }

    public static <T> MyResult<T> ok() {
        return ok(null);
    }

    public static <T> MyResult<T> ok(T data) {
        return ok(data, (BaseQuery)null);
    }

    public static <T> MyResult<T> fail(String errCode, String errMsg) {
        MyResult result = new MyResult();
        result.setSuccess(false);
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

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseQuery getQuery() {
        return this.query;
    }

    public void setQuery(BaseQuery query) {
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
