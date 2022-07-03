package dubbo.filter;

import org.apache.dubbo.rpc.*;
import dubbo.LogRecordContext;
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
@Activate(group = CommonConstants.PROVIDER, order = -10000)
public class RpcProviderContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isProviderSide()) {
            // 生产者
            LogRecordContext.onceRpcProviderLogRecord();
        }
        Object target = invocation.getInvoker().getInterface();
        Logger logger = LoggerFactory.getLogger(target.getClass());

        StringBuilder beginLogMsg = new StringBuilder();
        beginLogMsg.append("Enter RPC method ")
                .append(invocation.getInvoker().getInterface().getName())
                .append(".")
                .append(invocation.getMethodName())
                .append("()")
                .append(" args:")
                .append(JsonUtil.toJson(invocation.getArguments()));
        logger.info(beginLogMsg.toString());
        long startTime = System.currentTimeMillis();

        Result result = invoker.invoke(invocation);

        long elapsed = System.currentTimeMillis() - startTime;

        StringBuilder endLogMsg = new StringBuilder();
        endLogMsg.append("Exit RPC method ")
                .append(invocation.getInvoker().getInterface().getName())
                .append(".")
                .append(invocation.getMethodName())
                .append("()")
                .append(" result:{},")
                .append("use time:{}");

        logger.info(endLogMsg.toString(), JsonUtil.toJson(result.getValue()), elapsed);
        return  result;
    }
}
