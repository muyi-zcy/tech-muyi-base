package tech.muyi.core.format;

import io.opentracing.propagation.TextMap;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.core.constants.TracerConstants;
import tech.muyi.core.context.MySpanContext;
import tech.muyi.core.tracer.TraceIdGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/12/28
 **/
public abstract class AbstractTextMapFormatter implements BaseFormatter<TextMap> {


    @Override
    public MySpanContext extract(TextMap carrier) {
        if (carrier == null) {
            return null;
        }
        String traceId = null;
        String spanId = null;
        Map<String, String> baggage = new HashMap<>();


        MySpanContext mySpanContext = null;
        for (Map.Entry<String, String> entry : carrier) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isBlank(key)) {
                continue;
            }
            if (TracerConstants.TRACER_ID.equals(entry.getKey())) {
                traceId = value;
            } else if (TracerConstants.SPAN_ID.equals(entry.getKey())) {
                spanId = value;
            } else if (entry.getKey().startsWith(TracerConstants.BAGGAGE_KEY_PREFIX)) {
                baggage.put(key.substring((TracerConstants.BAGGAGE_KEY_PREFIX.length())), value);
            }
        }


        if (traceId == null) {
            return MySpanContext.rootStart();
        }

        if (spanId == null) {
            spanId = TracerConstants.ROOT_SPAN_ID;
        }

        mySpanContext = new MySpanContext(traceId, spanId, baggage);
        return mySpanContext;
    }

    @Override
    public void inject(MySpanContext mySpanContext, TextMap carrier) {
        for (Map.Entry<String, String> entry : mySpanContext.baggageItems()) {
            carrier.put(TracerConstants.BAGGAGE_KEY_PREFIX + entry.getKey(), entry.getValue());
        }
        carrier.put(TracerConstants.SPAN_ID, String.valueOf(mySpanContext.spanId()));
        carrier.put(TracerConstants.TRACER_ID, String.valueOf(mySpanContext.traceId()));
    }
}
