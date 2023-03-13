package tech.muyi.okhttp.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import tech.muyi.core.MyTracer;

import java.io.IOException;

/**
 * @author: muyi
 * @date: 2023/1/30
 **/
@Slf4j
public class MyLogOkHttpInterceptor implements okhttp3.Interceptor {
    private MyTracer myTracer;
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

//        Response response = chain.proceed(OkHttpRequestSpanTags(request, sofaTracerSpan));


//        return response;
        return null;
    }
}
