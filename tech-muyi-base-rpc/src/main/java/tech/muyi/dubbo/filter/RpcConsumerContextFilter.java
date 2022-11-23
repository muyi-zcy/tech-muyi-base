package tech.muyi.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import tech.muyi.dubbo.RpcLogRecordContext;

/**
 * description: ConsumerContextFilter
 * date: 2021/11/14 2:15
 * author: muyi
 * version: 1.0
 */
@Activate(group = {CommonConstants.CONSUMER})
public class RpcConsumerContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isConsumerSide()) {
            // 消费者
            RpcLogRecordContext.onceRpcConsumerLogRecord();
        }
        return invoker.invoke(invocation);
    }

}