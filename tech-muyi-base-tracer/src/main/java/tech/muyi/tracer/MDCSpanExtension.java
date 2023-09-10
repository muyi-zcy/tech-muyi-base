package tech.muyi.tracer;

import com.alipay.common.tracer.core.context.span.SofaTracerSpanContext;
import com.alipay.common.tracer.core.extensions.SpanExtension;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import io.opentracing.Span;
import org.slf4j.MDC;
import tech.muyi.tracer.constants.MDCKeyConstants;

/**
 * 自定义拓展
 */
public class MDCSpanExtension implements SpanExtension {

    @Override
    public void logStartedSpan(Span currentSpan) {
        if (currentSpan != null) {
            SofaTracerSpan span = (SofaTracerSpan) currentSpan;
            SofaTracerSpanContext sofaTracerSpanContext = span.getSofaTracerSpanContext();
            if (sofaTracerSpanContext != null) {
                MDC.put(MDCKeyConstants.MDC_TRACEID, sofaTracerSpanContext.getTraceId());
                MDC.put(MDCKeyConstants.MDC_SPANID, sofaTracerSpanContext.getSpanId());
            }
        }

    }

    @Override
    public void logStoppedSpan(Span currentSpan) {
        MDC.remove(MDCKeyConstants.MDC_TRACEID);
        MDC.remove(MDCKeyConstants.MDC_SPANID);
        if (currentSpan != null) {
            SofaTracerSpan span = (SofaTracerSpan) currentSpan;
            SofaTracerSpan parentSpan = span.getParentSofaTracerSpan();
            if (parentSpan != null) {
                SofaTracerSpanContext sofaTracerSpanContext = parentSpan.getSofaTracerSpanContext();
                if (sofaTracerSpanContext != null) {
                    MDC.put(MDCKeyConstants.MDC_TRACEID, sofaTracerSpanContext.getTraceId());
                    MDC.put(MDCKeyConstants.MDC_SPANID, sofaTracerSpanContext.getSpanId());
                }
            }
        }
    }

    @Override
    public void logStoppedSpanInRunnable(Span currentSpan) {
        MDC.remove(MDCKeyConstants.MDC_TRACEID);
        MDC.remove(MDCKeyConstants.MDC_SPANID);
    }

    @Override
    public String supportName() {
        return "slf4jmdc";
    }

}