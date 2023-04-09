package tech.muyi.core.async;

import tech.muyi.core.MySpan;
import tech.muyi.core.MyTracer;
import tech.muyi.util.ApplicationContextUtil;

/**
 * @author: muyi
 * @date: 2023/1/17
 **/
public class MyTraceAsyncSupport extends AbstractAsyncSupport {

    private final MySpan childSpan;

    private final long tid = Thread.currentThread().getId();

    private MyTracer myTracer = null;

    public MyTraceAsyncSupport(MyTracer myTracer) {
        this.myTracer = myTracer;
        MySpan span = myTracer.activeSpan();
        childSpan = myTracer.buildSpan().asChildOf(span).start();
    }

    @Override
    public void doBefore() {
        if (Thread.currentThread().getId() != tid && childSpan != null) {
            myTracer.activateSpan(childSpan);
        }
    }

    @Override
    public void doFinally() {
        if (childSpan != null) {
            childSpan.finish();
            myTracer.scopeManager() .close();
        }
    }
}
