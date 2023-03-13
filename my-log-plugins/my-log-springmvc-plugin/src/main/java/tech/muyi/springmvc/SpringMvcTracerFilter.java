package tech.muyi.springmvc;

import io.opentracing.tag.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.muyi.core.MySpan;
import tech.muyi.core.constants.TracerConstants;
import tech.muyi.core.context.MySpanContext;
import tech.muyi.util.ApplicationContextUtil;
import tech.muyi.util.request.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Map;

/**
 * SpringMvc 过滤器
 *
 * @author: muyi
 * @date: 2022/12/21
 **/
public class SpringMvcTracerFilter implements Filter {
    private final SpringMvcTracerClient springMvcTracerClient;


    public SpringMvcTracerFilter(SpringMvcTracerClient springMvcTracerClient){
        this.springMvcTracerClient = springMvcTracerClient;
    }



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        int httpStatus = -1;

        MySpan mySpan;

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        // 获取 http header 携带的信息
        Map<String, String> headerMap = RequestUtil.getHeaderMap(httpServletRequest);

        MySpanContext mySpanContext = springMvcTracerClient.getSpanContextSpringMvc(headerMap);
        mySpan = springMvcTracerClient.serverReceive(httpServletRequest.getRequestURI(), mySpanContext);

        mySpan.setTag(Tags.HTTP_URL.getKey(), httpServletRequest.getRequestURI());
        mySpan.setTag(Tags.HTTP_METHOD.getKey(), httpServletRequest.getMethod());


        try {
            HttpServletResponseWrapper httpServletResponseWrapper = new HttpServletResponseWrapper(httpServletResponse);
            filterChain.doFilter(httpServletRequest, httpServletResponseWrapper);
            httpStatus = httpServletResponseWrapper.getStatus();
        }catch (Throwable throwable){
            httpStatus = TracerConstants.HTTP_ERROR_STATUS;
            mySpan.log(throwable.getMessage());
            throw new RuntimeException(throwable);
        }finally {
            springMvcTracerClient.serverSend(String.valueOf(httpStatus));
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
