package tech.muyi.core.async;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.spi.TtlEnhanced;

import javax.annotation.Nullable;

/**
 * @author: muyi
 * @date: 2023/1/17
 **/
public class MyTraceRunnable implements Runnable{

    private final AbstractAsyncSupport abstractAsyncSupport;

    private final Runnable runnable;

    public MyTraceRunnable(Runnable runnable, AbstractAsyncSupport abstractAsyncSupport){
        this.runnable = runnable;
        this.abstractAsyncSupport = abstractAsyncSupport;
    }

    public static MyTraceRunnable get(Runnable runnable, AbstractAsyncSupport abstractAsyncSupport) {
        return new MyTraceRunnable(runnable, abstractAsyncSupport);
    }


    @Override
    public void run() {
        abstractAsyncSupport.doBefore();
        try {
            runnable.run();
        }finally {
            abstractAsyncSupport.doFinally();
        }
    }
}
