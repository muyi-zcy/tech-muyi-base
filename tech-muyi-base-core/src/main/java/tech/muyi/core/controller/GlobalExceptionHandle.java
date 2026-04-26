package tech.muyi.core.controller;

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
 *
 * <p>设计要点：
 * <ul>
 *   <li>业务异常（{@link MyException}）与未知异常分流，保证对外返回的错误码/提示可控。</li>
 *   <li>业务异常统一按 HTTP 200 返回（见 {@link ResponseStatus}），将“是否成功”交由响应体字段表达，
 *       便于网关/客户端统一处理，但同时要求调用方不要仅依据 HTTP 状态码判断成功。</li>
 * </ul>
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
    public MyResult<String> handleMyException(MyException myException) {
        // 业务异常：通常代表“已知可预期”的失败场景（参数/状态/权限等），记录后返回统一失败响应。
        log.error("业务处理异常:", myException);
        return MyResult.fail(myException);
    }

    @ExceptionHandler({Throwable.class})
    @ResponseBody
    public MyResult<Object> handleThrowable(Throwable throwable) {
        // 未知异常：尽量避免把堆栈/内部实现细节直接暴露给调用方，包装为 UnknownException 统一对外语义。
        log.error("内部未知异常", throwable);
        return MyResult.fail(new UnknownException(throwable));
    }
}
