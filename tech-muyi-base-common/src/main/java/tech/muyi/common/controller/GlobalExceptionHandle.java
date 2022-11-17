package tech.muyi.common.controller;

import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常拦截
 * @Author: muyi
 * @Date: 2021/1/3 21:58
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler({MyException.class})
    @ResponseBody
    @ResponseStatus(
            code = HttpStatus.OK
    )
    public MyResult<String> handleSntException(MyException e) {
        log.error("业务处理异常:", e);
        return MyResult.fail(e);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public MyResult<Object> handleException(Exception e) {
        log.error("内部未知异常", e);
        return MyResult.fail(new UnknownException(e));
    }
}
