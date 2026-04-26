package tech.muyi.id.constant;

/**
 * ID 模块系统参数键。
 *
 * <p>支持通过 JVM 参数 `-DMY_WORKERID`、`-DMY_DATACENTERID`
 * 显式指定雪花节点标识，避免多实例冲突。</p>
 *
 * @author: muyi
 * @date: 2022/11/24
 **/
public interface Constants {
    String WORKER_ID = "MY_WORKERID";
    String DATACENTER_ID = "MY_DATACENTERID";
}
