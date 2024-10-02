package tech.muyi.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.filter.ClusterFilter;
import tech.muyi.util.ttl.MyTtlContextManager;


/**
 * date: 2021/11/14 2:15
 * author: muyi
 */
@Activate(group = {CommonConstants.CONSUMER})
public class RpcConsumerContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isConsumerSide()) {
            RpcContext rpcContext = RpcContext.getContext();
            rpcContext.setAttachments(MyTtlContextManager.downAllData());
        }
        return invoker.invoke(invocation);
    }

}