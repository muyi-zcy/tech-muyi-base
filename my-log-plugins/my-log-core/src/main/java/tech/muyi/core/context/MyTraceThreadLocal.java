package tech.muyi.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import tech.muyi.core.MySpan;
import tech.muyi.core.MyTracer;
import tech.muyi.core.constants.TracerTypeEnum;
import tech.muyi.core.tracer.MyTracerBuilder;
import tech.muyi.util.ApplicationContextUtil;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class MyTraceThreadLocal<T> extends TransmittableThreadLocal<MyTtlScope> {
    private static MyTracer myTracer;

    public MyTraceThreadLocal() {
        super();
    }

    @Override
    public MyTtlScope copy(MyTtlScope parentValue) {
        return childTracer(parentValue);
    }

    @Override
    protected MyTtlScope childValue(MyTtlScope parentValue) {
        return childTracer(parentValue);
    }

    public MyTtlScope childTracer(MyTtlScope parentValue){
        if (parentValue == null || parentValue.span() == null) {
            return null;
        }

        MyTracer newTracer = new  MyTracerBuilder().build();

        MySpan parentSpan = parentValue.span();

        MySpan newSpan = newTracer.buildSpan().asChildOf(parentSpan).start();

        return newTracer.scopeManager().activate(newSpan);
    }

    @Override
    public void beforeExecute() {
    }

    @Override
    public void afterExecute() {
        MyTtlScope myTtlScope = super.get();
        if (myTtlScope == null) {
            return;
        }
        if (myTtlScope.span() != null) {
            myTtlScope.span().finish();
            myTtlScope.close();
        }
    }
}
