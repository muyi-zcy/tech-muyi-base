package tech.muyi.core.format;

import io.opentracing.propagation.Format;
import tech.muyi.core.context.MySpanContext;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
public interface BaseFormatter<T> {

    Format<T> getFormatType();

    MySpanContext extract(T carrier);

    void inject(MySpanContext mySpanContext, T carrier);
}
