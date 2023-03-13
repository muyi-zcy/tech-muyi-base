package tech.muyi.core;

import io.opentracing.*;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tag;
import tech.muyi.core.constants.TracerConstants;
import tech.muyi.core.context.MySpanContext;
import tech.muyi.core.context.MyTtlScope;
import tech.muyi.core.context.MyTtlScopeManager;
import tech.muyi.core.format.FormatRegistry;
import tech.muyi.core.span.Reference;
import tech.muyi.core.tracer.TraceIdGenerator;
import tech.muyi.core.utils.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: muyi
 * @date: 2022/12/20
 **/
public class MyTracer implements Tracer {
    private final List<MySpan> finishedSpans = new ArrayList<>();

    private final Map<String, Object> tags = new ConcurrentHashMap<>();

    // Active Span的容器
    private final MyTtlScopeManager scopeManager;

    private boolean isClosed;


    public MyTracer(MyTtlScopeManager scopeManager, Map<String, Object> tags) {
        this.scopeManager = scopeManager;
        if (tags != null) {
            this.tags.putAll(tags);
        }
    }

    @Override
    public MyTtlScopeManager scopeManager() {
        return this.scopeManager;
    }

    /**
     * 返回当前 激活（active）状态Span, 无则返回null
     *
     * @return SPAN
     */
    @Override
    public MySpan activeSpan() {
        return this.scopeManager.activeSpan();
    }

    /**
     * 激活当前 Span. 返回Scope（可以理解为 代表当前 Span 活跃的一个阶段）
     *
     * @param span SPAN
     * @return 返回Scope
     */
    @Override
    public MyTtlScope activateSpan(Span span) {
        return this.scopeManager().activate(span);
    }

    @Override
    public MySpanBuilder buildSpan(String operationName) {
        return new MySpanBuilder(operationName);
    }

    public MySpanBuilder buildSpan() {
        return new MySpanBuilder(StringUtil.EMPTY_STRING);
    }


    @Override
    public <C> void inject(SpanContext spanContext, Format<C> format, C c) {
        FormatRegistry.getRegistry(format).inject((MySpanContext) spanContext, c);
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C c) {
        return FormatRegistry.getRegistry(format).extract(c);
    }


    public synchronized void reset() {
        this.finishedSpans.clear();
    }


    synchronized void appendFinishedSpan(MySpan mySpan) {
        if (isClosed) {
            return;
        }
        this.finishedSpans.add(mySpan);
        this.onSpanFinished(mySpan);
    }

    public synchronized List<MySpan> finishedSpans() {
        return new ArrayList<>(this.finishedSpans);
    }


    protected void onSpanFinished(MySpan mySpan) {
    }

    public SpanContext activeSpanContext() {
        Span span = activeSpan();
        if (span == null) {
            return null;
        }

        return span.context();
    }

    @Override
    public void close() {
        this.isClosed = true;
        this.finishedSpans.clear();
    }

    public void log(String event) {
        this.activeSpan().log(event);
    }


    /**
     * span 构造器
     */
    public class MySpanBuilder implements Tracer.SpanBuilder {
        private final String operationName;

        private long startMicros;

        private List<Reference> references = new ArrayList<>();

        private boolean ignoringActiveSpan;

        private Map<String, Object> initialTags = new HashMap<>();

        /**
         * 生成构造器，并指定 Span 的 operationName
         *
         * @param operationName operationName
         */
        MySpanBuilder(String operationName) {
            this.operationName = operationName;
        }

        /**
         * @return builder
         */
        @Override
        public MySpanBuilder asChildOf(SpanContext parent) {
            return addReference(References.CHILD_OF, parent);
        }

        /**
         * @param parent
         * @return builder
         */
        @Override
        public MySpanBuilder asChildOf(Span parent) {
            if (parent == null) {
                return this;
            }
            return addReference(References.CHILD_OF, parent.context());
        }

        @Override
        public MySpanBuilder ignoreActiveSpan() {
            ignoringActiveSpan = true;
            return this;
        }

        @Override
        public MySpanBuilder addReference(String referenceType, SpanContext referencedContext) {
            if (!References.CHILD_OF.equals(referenceType) && !References.FOLLOWS_FROM.equals(referenceType)) {
                return this;
            }

            if (referencedContext != null) {
                this.references.add(new Reference((MySpanContext) referencedContext, referenceType));
            }
            return this;
        }

        @Override
        public MySpanBuilder withTag(String key, String value) {
            this.initialTags.put(key, value);
            return this;
        }

        @Override
        public MySpanBuilder withTag(String key, boolean value) {
            this.initialTags.put(key, value);
            return this;
        }

        @Override
        public MySpanBuilder withTag(String key, Number value) {
            this.initialTags.put(key, value);
            return this;
        }

        @Override
        public <T> MySpanBuilder withTag(Tag<T> tag, T value) {
            this.initialTags.put(tag.getKey(), value);
            return this;
        }

        @Override
        public MySpanBuilder withStartTimestamp(long microseconds) {
            this.startMicros = microseconds;
            return this;
        }

        @Override
        public MySpan start() {
            if (this.startMicros == 0) {
                this.startMicros = MySpan.nowMicros();
            }

            SpanContext activeSpanContext = MyTracer.this.activeSpanContext();
            if (this.references.isEmpty() && !this.ignoringActiveSpan && activeSpanContext != null) {
                this.references.add(new Reference((MySpanContext) activeSpanContext, References.CHILD_OF));
            }

            return new MySpan(MyTracer.this, operationName, startMicros, references, initialTags);
        }
    }

}
