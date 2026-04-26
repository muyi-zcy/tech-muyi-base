package tech.muyi.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.filter.ClusterFilter;
import tech.muyi.util.ttl.MyTtlContextManager;


/**
 * Dubbo 消费端上下文透传过滤器。
 *
 * <p>在 consumer 侧调用前，把本地 TTL 上下文下传到 RPC attachments，
 * 用于 provider 侧恢复链路信息（如 requestId、用户上下文）。</p>
 *
 * date: 2021/11/14 2:15
 * author: muyi
 */
@Activate(group = {CommonConstants.CONSUMER})
public class RpcConsumerContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isConsumerSide()) {
            RpcContext rpcContext = RpcContext.getContext();
            // 批量下传当前线程上下文，避免逐字段硬编码。
            rpcContext.setAttachments(MyTtlContextManager.downAllData());
        }
        return invoker.invoke(invocation);
    }

}