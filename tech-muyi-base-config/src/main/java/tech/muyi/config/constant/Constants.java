package tech.muyi.config.constant;

/**
 * 配置模块常量定义。
 *
 * <p>`NODE_ID` 对应 JVM 系统属性键，支持通过 `-DMY_NODEID=...`
 * 在启动时指定集群内稳定节点标识。</p>
 *
 * @author: muyi
 * @date: 2022/11/24
 **/
public interface Constants {
    String NODE_ID = "MY_NODEID";
}
