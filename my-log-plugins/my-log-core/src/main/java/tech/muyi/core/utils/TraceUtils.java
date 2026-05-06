package tech.muyi.core.utils;

import com.alipay.common.tracer.core.context.trace.SofaTraceContext;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;

/**
 * 链路追踪工具类
 *
 * @author muyi
 */
public class TraceUtils {

    /**
     * 获取当前 TraceId
     *
     * @return TraceId，如果不存在则返回空字符串
     */
    public static String getCurrentTraceId() {
        SofaTracerSpan currentSpan = getCurrentSpan();
        if (currentSpan != null) {
            return currentSpan.getSofaTracerSpanContext().getTraceId();
        }
        return "";
    }

    /**
     * 获取当前 SpanId
     *
     * @return SpanId，如果不存在则返回空字符串
     */
    public static String getCurrentSpanId() {
        SofaTracerSpan currentSpan = getCurrentSpan();
        if (currentSpan != null) {
            return currentSpan.getSofaTracerSpanContext().getSpanId();
        }
        return "";
    }

    /**
     * 获取当前 Span
     *
     * @return 当前 Span，如果不存在则返回 null
     */
    public static SofaTracerSpan getCurrentSpan() {
        SofaTraceContext traceContext = SofaTraceContextHolder.getSofaTraceContext();
        if (traceContext != null) {
            return traceContext.getCurrentSpan();
        }
        return null;
    }

    /**
     * 判断当前是否在追踪上下文中
     *
     * @return true 如果存在追踪上下文
     */
    public static boolean isInTraceContext() {
        return getCurrentSpan() != null;
    }

    /**
     * 格式化参数（限制长度）
     *
     * @param parameters 参数对象
     * @param maxLength  最大长度
     * @return 格式化后的字符串
     */
    public static String formatParameters(Object parameters, int maxLength) {
        if (parameters == null) {
            return "";
        }

        String paramStr = parameters.toString();
        if (paramStr.length() > maxLength) {
            return paramStr.substring(0, maxLength) + "...";
        }
        return paramStr;
    }

    /**
     * 格式化异常堆栈（限制长度）
     *
     * @param throwable 异常对象
     * @param maxLength 最大长度
     * @return 格式化后的堆栈字符串
     */
    public static String formatStackTrace(Throwable throwable, int maxLength) {
        if (throwable == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            sb.append("\n");
            int count = Math.min(stackTrace.length, 10); // 最多取前 10 行
            for (int i = 0; i < count; i++) {
                sb.append("\tat ").append(stackTrace[i].toString()).append("\n");
            }
        }

        String result = sb.toString();
        if (result.length() > maxLength) {
            return result.substring(0, maxLength) + "...";
        }
        return result;
    }
}
