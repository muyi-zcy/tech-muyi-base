package tech.muyi.springmvc;

import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import org.springframework.stereotype.Service;
import tech.muyi.core.MyTracer;
import tech.muyi.core.constants.TracerTypeEnum;
import tech.muyi.core.context.MySpanContext;
import tech.muyi.core.tracer.AbstractTracerClient;

import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/12/21
 **/
public class SpringMvcTracerClient extends AbstractTracerClient {

    public SpringMvcTracerClient(MyTracer myTracer) {
        super(myTracer);
    }

    public MySpanContext getSpanContextSpringMvc(Map<String,String> injectMap){
        return this.getSpanContext(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(injectMap));
    }
}
