package tech.muyi.core.context;

import io.opentracing.SpanContext;
import tech.muyi.core.constants.TracerConstants;
import tech.muyi.core.tracer.TraceIdGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: muyi
 * @date: 2022/12/20
 **/
public class MySpanContext implements SpanContext {

    private final AtomicLong nextId = new AtomicLong(0);

    private final String traceId;

    private final String spanId;

    //    Trace的随行数据，是一个键值对集合，它存在于trace中，也需要跨进程边界传输
    private final Map<String, String> baggage;

    public MySpanContext(String traceId, Map<String, String> baggage) {
        this.baggage = baggage;
        this.traceId = traceId;
        this.spanId = String.valueOf(nextId.incrementAndGet());
    }

    public MySpanContext(String traceId, String spanId, Map<String, String> baggage) {
        this.baggage = baggage;
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public String getBaggageItem(String key) {
        return this.baggage.get(key);
    }

    public String toTraceId() {
        return traceId;
    }

    public String toSpanId() {
        return spanId;
    }

    public String traceId() {
        return traceId;
    }

    public String spanId() {
        return spanId;
    }

    public Map<String, String> baggage() {
        return baggage;
    }

    public MySpanContext withBaggageItem(String key, String val) {
        Map<String, String> newBaggage = new HashMap<>(this.baggage);
        newBaggage.put(key, val);
        return new MySpanContext(this.traceId, this.spanId, newBaggage);
    }

    public MySpanContext setBaggage(Map<String, String> newBaggage) {
        if (newBaggage != null && newBaggage.size() > 0) {
            this.baggage.putAll(newBaggage);
        }
        return this;
    }


    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return baggage.entrySet();
    }

    public static MySpanContext rootStart() {
        return new MySpanContext(TraceIdGenerator.getInitTraceId(), TracerConstants.ROOT_SPAN_ID, new HashMap<>());
    }


    /**
     * 获取子span的spanId
     *
     * @return child spanId
     */
    public String nextChildSpanId() {
        return this.spanId + TracerConstants.SPAN_LINK + nextId.incrementAndGet();
    }


}
