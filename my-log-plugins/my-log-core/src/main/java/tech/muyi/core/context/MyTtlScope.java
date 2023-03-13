package tech.muyi.core.context;

import io.opentracing.Scope;
import io.opentracing.Span;
import tech.muyi.core.MySpan;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class MyTtlScope implements Scope {
    private final MyTtlScopeManager scopeManager;
    private final MySpan wrapped;
    private final MyTtlScope toRestore;

    MyTtlScope(MyTtlScopeManager scopeManager, MySpan wrapped) {
        this.scopeManager = scopeManager;
        this.wrapped = wrapped;
        this.toRestore = scopeManager.tlsScope.get();
        scopeManager.tlsScope.set(this);
    }

    @Override
    public void close() {
        if (this.scopeManager.tlsScope.get() == this) {
            this.scopeManager.tlsScope.set(this.toRestore);
        }
    }


    MySpan span() {
        return this.wrapped;
    }
}
