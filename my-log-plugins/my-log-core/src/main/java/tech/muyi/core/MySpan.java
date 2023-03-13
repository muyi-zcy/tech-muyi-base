package tech.muyi.core;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.tag.Tag;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import tech.muyi.core.constants.TracerConstants;
import tech.muyi.core.context.MySpanContext;
import tech.muyi.core.span.Log;
import tech.muyi.core.span.Reference;
import tech.muyi.core.tracer.TraceIdGenerator;
import tech.muyi.core.utils.StringUtil;
import tech.muyi.util.JsonUtil;

import java.util.*;

/**
 * @author: muyi
 * @date: 2022/12/20
 **/
@Data
public class MySpan implements Span {

    private transient final MyTracer myTracer;

    //    一组键值对构成的Span标签集合。键值对中，键必须为string，值可以是字符串，布尔，或者数字类型。
    private final Map<String, Object> tags = new LinkedHashMap<>();

    //    一组span的日志集合。每次log操作包含一个键值对，以及一个时间戳。键值对中，键必须为string，值可以是任意类型
    private final List<Log> logs = new LinkedList<>();

    // 操作名称
    private String operationName;

    //    Span上下文对象
    private MySpanContext mySpanContext;
    // 起始时间
    private final long startMicros;

    // 结束状态
    private boolean finished;

    //结束时间
    private long finishMicros;

    private final String parentId;

    // 相关的零个或者多个Span
    private final List<Reference> references;


    public MySpan(MyTracer myTracer, String operationName, long startMicros, List<Reference> references, Map<String, Object> initialTags) {
        this.myTracer = myTracer;
        this.operationName = operationName;
        this.startMicros = startMicros;

        if (initialTags != null) {
            for (Map.Entry<String, Object> tag : initialTags.entrySet()) {
                setObjectTag(tag.getKey(), tag.getValue());
            }
        }

        if (CollectionUtils.isEmpty(references)) {
            this.references = Collections.emptyList();
        } else {
            this.references = new ArrayList<>(references);
        }

        MySpanContext parent = findPreferredParentRef(this.references);
        if (parent == null) {
            this.mySpanContext = new MySpanContext(TraceIdGenerator.getInitTraceId(), new HashMap<>());
            this.parentId = TracerConstants.ROOT_SPAN_ID;
        } else {
            this.mySpanContext = new MySpanContext(parent.traceId(), parent.nextChildSpanId(), mergeBaggages(this.references));
            this.parentId = parent.spanId();
        }

    }

    @Override
    public MySpanContext context() {
        return this.mySpanContext;
    }

    @Override
    public MySpan setTag(String key, String value) {
        return setObjectTag(key, value);
    }

    @Override
    public MySpan setTag(String key, boolean value) {
        return setObjectTag(key, value);
    }

    @Override
    public MySpan setTag(String key, Number value) {
        return setObjectTag(key, value);
    }

    @Override
    public <T> MySpan setTag(Tag<T> tag, T t) {
        tag.set(this, t);
        return this;
    }

    private synchronized MySpan setObjectTag(String key, Object value) {
        tags.put(key, value);
        return this;
    }

    @Override
    public MySpan log(Map<String, ?> fields) {
        return log(nowMicros(), fields);
    }

    @Override
    public MySpan log(String event) {
        return this.log(nowMicros(), event);
    }

    @Override
    public MySpan log(long timestampMicroseconds, String event) {
        return this.log(timestampMicroseconds, Collections.singletonMap(Log.EVENT_KEY, event));
    }

    @Override
    public MySpan log(long timestampMicroseconds, Map<String, ?> fields) {
        synchronized (this) {
            logs.add(new Log(timestampMicroseconds, fields));
            return this;
        }
    }


    @Override
    public synchronized Span setBaggageItem(String key, String value) {
        this.mySpanContext = this.mySpanContext.withBaggageItem(key, value);
        return this;
    }

    @Override
    public String getBaggageItem(String key) {
        return this.mySpanContext.getBaggageItem(key);
    }


    @Override
    public Span setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    @Override
    public void finish() {
        this.finish(nowMicros());
    }

    @Override
    public void finish(long finishMicros) {
        this.setFinishMicros(finishMicros);
//        this.myTracer.appendFinishedSpan(this);
        this.finished = true;
        System.out.println(JsonUtil.toJson(this));
        mdc();
    }

    /**
     * 关键信息放入当前线程的诊断上下文映射中
     */
    public void mdc() {
        MySpan mySpan = this.myTracer.activeSpan();
        if(mySpan != null){
            MDC.put(TracerConstants.LOG_SPANID, mySpan.mySpanContext.toSpanId());
            MDC.put(TracerConstants.LOG_TRACEID, mySpan.mySpanContext.toTraceId());
        }else {
            MDC.clear();
        }
    }


    public static long nowMicros() {
        return System.currentTimeMillis() * 1000;
    }

    private static MySpanContext findPreferredParentRef(List<Reference> references) {
        if (references.isEmpty()) {
            return null;
        }
        for (Reference reference : references) {
            if (References.CHILD_OF.equals(reference.getReferenceType())) {
                return reference.getContext();
            }
        }
        return references.get(0).getContext();
    }

    private static Map<String, String> mergeBaggages(List<Reference> references) {
        Map<String, String> baggage = new HashMap<>();
        for (Reference ref : references) {
            if (ref.getContext().baggage() != null) {
                baggage.putAll(ref.getContext().baggage());
            }
        }
        return baggage;
    }


    public MyTracer getMyTracer() {
        return myTracer;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public String getOperationName() {
        return operationName;
    }

    public MySpanContext getMySpanContext() {
        return mySpanContext;
    }

    public void setMySpanContext(MySpanContext mySpanContext) {
        this.mySpanContext = mySpanContext;
    }

    public long getStartMicros() {
        return startMicros;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public long getFinishMicros() {
        return finishMicros;
    }

    public void setFinishMicros(long finishMicros) {
        this.finishMicros = finishMicros;
    }

    public String getParentId() {
        return parentId;
    }

    public List<Reference> getReferences() {
        return references;
    }

}
