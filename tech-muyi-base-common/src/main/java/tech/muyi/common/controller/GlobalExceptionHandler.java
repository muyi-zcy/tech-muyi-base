package tech.muyi.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tech.muyi.common.MyResult;
import tech.muyi.exception.MyException;
import tech.muyi.exception.UnknownException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.net.BindException;
import java.sql.SQLException;

/**
 * 全局异常处理
 * @Author: muyi
 * @Date: 2021/1/3 21:58
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MyException.class})
    @ResponseBody
    @ResponseStatus(
            code = HttpStatus.OK
    )
    public MyResult<String> handleSntException(MyException e) {
        logger.error("业务处理异常", e);
        return MyResult.fail(e);
    }

    @ExceptionHandler({HttpMessageConversionException.class})
    @ResponseBody
    public MyResult handler(HttpMessageConversionException e) {
        logger.error("HttpMessageConversionException 异常", e);
        return MyResult.fail(new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), "参数校验失败：数字不可为字母 或 时间格式不正确 或 数字过大"));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public MyResult handler(HttpMessageNotReadableException e) {
        logger.error("HttpMessageNotReadableException 异常", e);
        return MyResult.fail(new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), "参数校验失败：数字不可为字母 或 时间格式不正确 或 数字过大"));
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public MyResult exceptionHandler(HttpServletRequest req, NullPointerException e){
        logger.error("发生空指针异常！原因是:",e);
        return MyResult.fail(new MyException(CommonErrorCodeEnum.NULL_POINTER.getResultCode(), CommonErrorCodeEnum.NULL_POINTER.getResultMsg()));

    }

    @ExceptionHandler({BindException.class})
    @ResponseBody
    public MyResult handler(BindException e) {
        logger.error("BindException 异常", e);
        return MyResult.fail(new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), "参数校验失败：数字不可为字母 或 时间格式不正确 或 数字过大"));
    }

    @ExceptionHandler({SQLException.class})
    @ResponseBody
    public MyResult<String> handSql(Exception e) {
        logger.error("SQL Exception ", e);
        return MyResult.fail(new MyException(CommonErrorCodeEnum.DB_EXCEPTION.getResultCode(), "数据库执行异常"));
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public MyResult<Object> handleException(Exception e) {
        logger.error("内部未知异常", e);
        return MyResult.fail(new UnknownException(e));
    }
}
