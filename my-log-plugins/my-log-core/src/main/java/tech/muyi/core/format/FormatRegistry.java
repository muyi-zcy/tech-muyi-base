package tech.muyi.core.format;

import io.opentracing.propagation.Format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
public class FormatRegistry {
    private final static Map<Format<?>, BaseFormatter<?>> baseFormatterMap = new ConcurrentHashMap<>();

    static {
        TextMapFormatter textMapFormatter = new TextMapFormatter();
        HttpHeadersFormatter httpHeadersFormatter = new HttpHeadersFormatter();
        BinaryFormatter binaryFormatter = new BinaryFormatter();

        baseFormatterMap.put(textMapFormatter.getFormatType(), textMapFormatter);
        baseFormatterMap.put(httpHeadersFormatter.getFormatType(), httpHeadersFormatter);
        baseFormatterMap.put(binaryFormatter.getFormatType(), binaryFormatter);
    }

    public static <T> BaseFormatter<T> getRegistry(Format<T> format) {
        return (BaseFormatter<T>) baseFormatterMap.get(format);
    }
}
