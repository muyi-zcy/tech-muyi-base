package tech.muyi.core.tracer;

import io.opentracing.util.ThreadLocalScopeManager;
import tech.muyi.core.MyTracer;
import tech.muyi.core.context.MyTtlScopeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
public class MyTracerBuilder {

    private final Map<String, Object> tags = new HashMap<>();

    public MyTracerBuilder() {
    }

    public MyTracer build() {
        return new MyTracer(new MyTtlScopeManager(), tags);
    }

    public MyTracerBuilder withTag(String key, String value) {
        this.tags.put(key, value);
        return this;
    }

    public MyTracerBuilder withTag(String key, boolean value) {
        this.tags.put(key, value);
        return this;
    }

    public MyTracerBuilder withTag(String key, Number value) {
        this.tags.put(key, value);
        return this;
    }

    public MyTracerBuilder withTags(Map<String, String> tags) {
        if (tags != null) {
            this.tags.putAll(tags);
        }

        return this;
    }
}
