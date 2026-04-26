package tech.muyi.dubbo.filter;

import org.apache.dubbo.rpc.*;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.filter.ClusterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.muyi.util.MyJson;
import tech.muyi.util.ttl.MyTtlContextManager;

/**
 * Dubbo 服务端上下文恢复与日志过滤器。
 *
 * <p>provider 侧收到调用后先恢复 attachments 中的 TTL 上下文，
 * 再记录入参/出参与耗时，保证跨服务链路日志可串联。</p>
 *
 * date: 2021/11/14 2:31
 * author: muyi
 */
@Activate(group = {CommonConstants.PROVIDER})
public class RpcProviderContextFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isProviderSide()) {
            RpcContext rpcContext = RpcContext.getContext();
            // 恢复消费者透传的数据到当前线程上下文。
            MyTtlContextManager.upAllData(rpcContext.getAttachments());
        }
        Object target = invocation.getInvoker().getInterface();
        Logger logger = LoggerFactory.getLogger(target.getClass());

        String beginLogMsg = "Enter RPC method " +
                invocation.getInvoker().getInterface().getName() +
                "." +
                invocation.getMethodName() +
                "()" +
                " args:" +
                MyJson.toJson(invocation.getArguments());
        logger.info(beginLogMsg);
        long startTime = System.currentTimeMillis();

        Result result = invoker.invoke(invocation);

        long elapsed = System.currentTimeMillis() - startTime;

        String endLogMsg = "Exit RPC method " +
                invocation.getInvoker().getInterface().getName() +
                "." +
                invocation.getMethodName() +
                "()" +
                " result:{}," +
                "use time:{}";

        logger.info(endLogMsg, MyJson.toJson(result.getValue()), elapsed);
        return result;
    }
}
