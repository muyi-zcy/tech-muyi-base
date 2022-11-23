package tech.muyi.dubbo;


import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.rpc.RpcContext;
import lombok.Data;
import org.slf4j.MDC;
import tech.muyi.GlobalContext;
import tech.muyi.log.LogConstant;
import tech.muyi.log.LogRecordUtil;

import java.util.Map;

@Data
public class RpcLogRecordContext {

    /**
     * 消费者传入链路信息（调用方）
     */
    public static void onceRpcConsumerLogRecord() {
        RpcContext.getContext().setAttachment(LogConstant.TRACE_ID, LogRecordUtil.getTraceId());
        RpcContext.getContext().setAttachment(LogConstant.SPAN_ID, LogRecordUtil.getSpanId());
        RpcContext.getContext().setAttachment(LogConstant.LOGIC_ID, LogRecordUtil.incrLogicId(LogRecordUtil.getServiceNum()));
        RpcContext.getContext().setAttachment(LogConstant.GLOBAL_CONTEXT, GlobalContext.getGlobalContext());
    }

    /**
     * 生产者获取链路信息（被调用方）
     */
    public static void onceRpcProviderLogRecord() {
        String traceId = RpcContext.getContext().getAttachment(LogConstant.TRACE_ID);
        MDC.put(LogConstant.TRACE_ID, traceId);

        String logicId = RpcContext.getContext().getAttachment(LogConstant.LOGIC_ID);
        MDC.put(LogConstant.LOGIC_ID, logicId);
        MDC.put(LogConstant.SERVICE_NUM,"1");

        String spanId = RpcContext.getContext().getAttachment(LogConstant.SPAN_ID);
        MDC.put(LogConstant.SPAN_ID, LogRecordUtil.getNewSpanId(spanId, logicId));

        Object context =  RpcContext.getContext().getObjectAttachment(LogConstant.GLOBAL_CONTEXT);
        if(context != null && context instanceof Map) {
            Map<String, Object> globalContext = (Map<String, Object>) context;
            GlobalContext.setGlobalContext(globalContext);
        }
    }


    /**
     * 清理当前线程链路信息
     */
    public static void clearMDC() {
        MDC.clear();
    }
}
