package tech.muyi.core.format;

import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

/**
 * @author: muyi
 * @date: 2022/12/28
 **/
public class TextMapFormatter extends AbstractTextMapFormatter{
    @Override
    public Format<TextMap> getFormatType() {
        return Format.Builtin.TEXT_MAP;
    }
}
