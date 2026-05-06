package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 远程调用异常
 *
 * <p>用于RPC、HTTP等远程调用失败的场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class RemoteException extends SystemException {

    private String serviceName;
    private String methodName;

    public RemoteException(String serviceName, String methodName, Throwable cause) {
        super(CommonErrorCodeEnum.INTERFACE_OUTER_INVOKE_ERROR,
                String.format("调用远程服务失败: %s.%s", serviceName, methodName),
                cause);
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }
}
