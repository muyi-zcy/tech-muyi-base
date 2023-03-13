package tech.muyi.core.constants;

/**
 * @author: muyi
 * @date: 2022/12/21
 **/
public interface TracerConstants {
    String TRACER_ID = "X-MY-TracerId";

    String SPAN_ID = "X-MY-SpanId";

    String LOG_TRACEID = "traceId";

    String LOG_SPANID = "spanId";

    String BAGGAGE_KEY_PREFIX = "baggage-";

    Integer HTTP_ERROR_STATUS = 500;

    String SPAN_LINK = ".";

    String ROOT_SPAN_ID = "0";

}
