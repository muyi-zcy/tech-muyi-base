package tech.muyi.core.context;

import io.opentracing.ScopeManager;
import io.opentracing.Span;
import tech.muyi.core.MySpan;

/**
 * @author: muyi
 * @date: 2023/1/12
 **/
public class MyTtlScopeManager implements ScopeManager {
    final ThreadLocal<MyTtlScope> tlsScope = new ThreadLocal<>();

    @Override
    public MyTtlScope activate(Span span) {
        if (span instanceof MySpan) {
            MyTtlScope myTtlScope = new MyTtlScope(this, (MySpan) span);
            ((MySpan) span).mdc();
            return myTtlScope;
        }
        return null;
    }

    @Override
    public MySpan activeSpan() {
        MyTtlScope scope = this.tlsScope.get();
        return scope == null ? null : scope.span();
    }

    public void close() {
        MyTtlScope scope = this.tlsScope.get();
        if (scope != null) {
            scope.close();
        }
    }
}
