# id 模块使用说明

## 1. 模块定位

`tech-muyi-base-id` 提供统一的分布式 ID 生成能力，当前默认实现为雪花算法（Snowflake）。

典型用途：

- 数据库主键（`BIGINT`）
- 业务订单号、流水号（字符串形式）
- 跨服务需要唯一且趋势递增的标识

## 2. 核心能力

### 2.1 自动装配

模块通过自动配置注册 `MyIdGenerator`，默认实现为 `DefaultSnowflakeImpl`，业务服务引入依赖后可直接注入使用。

### 2.2 统一接口

`MyIdGenerator` 提供三类能力：

- `init()`：初始化（当前默认实现无额外初始化动作）
- `nextId()`：生成 `long` 类型 ID
- `nextIdStr()`：生成字符串类型 ID

### 2.3 生成策略（默认雪花实现）

- 时间位 + 数据中心位 + 机器位 + 序列位（同毫秒内自增）
- 支持时钟小幅回拨容忍（默认 2000ms）
- 支持自定义起始时间（`baseTime`）
- 支持自动推导 `dataCenterId` 与 `workerId`（未配置时兜底）

## 3. 依赖接入

如果你已经引入聚合模块（如 `tech-muyi-base-on`），通常无需单独引入。  
需要显式引入时，添加：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-id</artifactId>
</dependency>
```

## 4. 使用示例

### 4.1 注入并生成 ID

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.muyi.id.MyIdGenerator;

@Service
public class OrderIdService {

    @Autowired
    private MyIdGenerator myIdGenerator;

    public long nextOrderId() {
        return myIdGenerator.nextId();
    }

    public String nextOrderIdStr() {
        return myIdGenerator.nextIdStr();
    }
}
```

### 4.2 application.yml 配置示例

```yaml
muyi:
  id:
    snowflake:
      # 起始时间戳（毫秒）
      baseTime: 1672502400000
      # 机器号（0~31）
      workerId: 1
      # 数据中心号（0~31）
      dataCenterId: 1
      # true: 使用 hutool SystemClock；false: System.currentTimeMillis()
      useSystemClock: false
      # 可容忍时钟回拨毫秒数
      timeOffs: 2000
```

> 说明：Spring Boot `@ConfigurationProperties` 支持宽松绑定，`base-time` 与 `baseTime` 都可映射。本文档统一使用与 Java 字段一致的驼峰写法。

## 5. JVM 参数覆盖（可选）

当未配置 `muyi.id.snowflake` 时，模块支持通过 JVM 参数指定节点信息：

- `-DMY_WORKERID=1`
- `-DMY_DATACENTERID=1`

> 建议在多实例部署时显式配置节点号，避免不同实例取到相同节点标识导致冲突风险。

## 6. 运维与使用建议

- **节点唯一性**：同一部署域内，`workerId + dataCenterId` 组合必须唯一。
- **字段类型**：数据库字段建议使用 `BIGINT`（或字符串字段存 `nextIdStr()`）。
- **时钟稳定性**：生产环境开启 NTP，但避免大幅时间回拨。
- **容量规划**：默认位宽支持 32 个数据中心、每个数据中心 32 台节点、单节点每毫秒 4096 序列。

## 7. 常见问题

### 7.1 报错 `Clock moved backwards`

说明系统时钟回拨超过允许阈值（默认 2000ms）。  
可排查 NTP 校时策略，或结合场景适当调大 `timeOffs`。

### 7.2 多机部署如何避免冲突

优先显式配置每个实例的 `workerId` 与 `dataCenterId`；  
不要完全依赖自动推导结果，尤其在容器弹性扩缩场景中。
