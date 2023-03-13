package tech.muyi.core.tracer;

import io.opentracing.Span;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import tech.muyi.core.MyTracer;
import tech.muyi.core.MySpan;
import tech.muyi.core.context.MySpanContext;
import tech.muyi.core.span.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracer 操作抽象类
 *
 * @author: muyi
 * @date: 2022/12/21
 **/
public abstract class AbstractTracerClient {

    public MyTracer myTracer;

    public AbstractTracerClient(MyTracer myTracer){
        this.myTracer = myTracer;
    }

    public <C> MySpanContext getSpanContext(Format<C> format, C c) {
        return (MySpanContext) myTracer.extract(format, c);
    }

    /**
     * 客户端发送请求
     *
     * @param operationName 操作名
     */
    public MySpan clientSend(String operationName) {
        MySpan span = this.myTracer.activeSpan();
        MySpan clientSpan = null;
        try {
            clientSpan = this.myTracer.buildSpan(operationName).asChildOf(span).start();
        } catch (Throwable throwable) {
            clientSpan = this.spanError(span.getMySpanContext().baggage());
        } finally {
            this.myTracer.activateSpan(clientSpan);
        }
        return clientSpan;
    }

    public void clientReceive(String statusCode) {
        MySpan span = this.myTracer.activeSpan();
        if (span == null) {
            return;
        }
        span.setTag(Tags.HTTP_STATUS.getKey(), statusCode);

        // 恢复span
        this.myTracer.scopeManager().close();

    }

    /**
     * SR 处理逻辑
     *
     * @param operationName 操作名称
     * @param mySpanContext spanContext
     */
    public MySpan serverReceive(String operationName, MySpanContext mySpanContext) {
        // 获取当前active span
        MySpan newSpan = null;
        MySpan span = this.myTracer.activeSpan();
        try {
            // active span is null，激活新的span
            if (span == null) {
                newSpan = this.myTracer.buildSpan(operationName).asChildOf(mySpanContext).start();
            } else {
                newSpan = this.myTracer.buildSpan(operationName).asChildOf(span).start();
            }
        } catch (Throwable throwable) {
            // 出现异常，作为补偿重新生成span
            newSpan = this.spanError(mySpanContext.baggage());
        } finally {
            this.myTracer.activateSpan(newSpan);
        }

        return newSpan;
    }


    /**
     * SS 处理逻辑
     *
     * @param statusCode 服务端状态码
     */
    public void serverSend(String statusCode) {
        try {
            MySpan span = this.myTracer.activeSpan();
            if (span == null) {
                return;
            }
            span.setTag(Tags.HTTP_STATUS.getKey(), statusCode);
            // 恢复span
            this.myTracer.scopeManager().close();
            span.finish();
        } finally {
            finish();
        }
    }


    /**
     * span传递过程中出现异常的处理行为
     * @param baggage old baggage
     * @return new span
     */
    protected MySpan spanError(Map<String, String> baggage) {
        MySpanContext mySpanContext = MySpanContext.rootStart();
        mySpanContext.setBaggage(baggage);
        return this.myTracer.buildSpan().asChildOf(mySpanContext).start();
    }


    /**
     * 业务异常的错误日志记录
     * @param throwable throwable
     * @param span span
     */
    protected void onError(Throwable throwable, Span span) {
        Tags.ERROR.set(span, Boolean.TRUE);
        if (throwable != null) {
            span.log(errorLogs(throwable));
        }
    }


    protected Map<String, Object> errorLogs(Throwable throwable) {
        Map<String, Object> errorLogs = new HashMap<>(2);
        errorLogs.put(Log.EVENT_KEY, Tags.ERROR.getKey());
        errorLogs.put(Log.EVENT_OBJECT, throwable);
        return errorLogs;
    }

    public void finish() {
    }
}
