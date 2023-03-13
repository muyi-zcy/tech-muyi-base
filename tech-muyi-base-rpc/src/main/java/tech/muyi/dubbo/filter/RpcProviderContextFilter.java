package tech.muyi.dubbo.filter;

import org.apache.dubbo.rpc.*;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.muyi.util.JsonUtil;

/**
 * description: RpcProviderContextFilter
 * date: 2021/11/14 2:31
 * author: muyi
 * version: 1.0
 */
@Activate(group = {CommonConstants.PROVIDER})
public class RpcProviderContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (org.apache.dubbo.rpc.RpcContext.getContext().isProviderSide()) {

        }
        Object target = invocation.getInvoker().getInterface();
        Logger logger = LoggerFactory.getLogger(target.getClass());

        String beginLogMsg = "Enter RPC method " +
                invocation.getInvoker().getInterface().getName() +
                "." +
                invocation.getMethodName() +
                "()" +
                " args:" +
                JsonUtil.toJson(invocation.getArguments());
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

        logger.info(endLogMsg, JsonUtil.toJson(result.getValue()), elapsed);
        return result;
    }
}
