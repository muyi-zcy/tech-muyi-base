package tech.muyi.db;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tech.muyi.db.constant.ShardingErrorLogTags;

import java.util.HashMap;
import java.util.Map;

/**
 * Sharding error span.
 */
public final class ShardingErrorSpan {
    
    /**
     * Set error.
     * 
     * @param span span to be set
     * @param cause failure cause of span
     */
    public static void setError(final Span span, final Exception cause) {
        span.setTag(Tags.ERROR.getKey(), true).log(System.currentTimeMillis(), getReason(cause));
    }
    
    private static Map<String, ?> getReason(final Throwable cause) {
        Map<String, String> result = new HashMap<>(3, 1);
        result.put(ShardingErrorLogTags.EVENT, ShardingErrorLogTags.EVENT_ERROR_TYPE);
        result.put(ShardingErrorLogTags.ERROR_KIND, cause.getClass().getName());
        result.put(ShardingErrorLogTags.MESSAGE, cause.getMessage());
        return result;
    }
}
