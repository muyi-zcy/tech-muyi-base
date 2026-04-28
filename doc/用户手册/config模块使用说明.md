# config 模块使用说明

## 1. 模块定位

`tech-muyi-base-config` 负责系统级配置能力的统一管理，核心目标是让配置“可集中、可治理、可排障”。

主要覆盖：

- 常量统一定义；
- 配置属性映射；
- 启动初始化流程收敛。

## 2. 核心能力

### 2.1 常量集中管理

通过 `Constants` 统一维护系统通用常量，避免各模块复制同名常量导致语义漂移。

### 2.2 配置映射规范

通过 `MyProperties` 等属性对象承接配置项，替代业务代码中大量散落的 `@Value`。

收益：

- 配置结构更清晰；
- 可进行统一默认值管理；
- 启动时更容易发现缺失项。

### 2.3 初始化流程统一

通过 `InitConfiguration` 收敛系统初始化逻辑，便于：

- 控制初始化顺序；
- 统一打印启动诊断日志；
- 降低各服务“各自初始化”的维护成本。

## 3. 依赖接入

如已引入 `tech-muyi-base-on`，通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-config</artifactId>
</dependency>
```

## 4. 配置实践建议

- **按域分组**：配置前缀按业务域拆分，如 `muyi.xxx`，避免平铺键名。
- **补齐四元信息**：每个配置项尽量说明默认值、是否必填、示例值、影响范围。
- **避免硬编码**：业务逻辑中不要硬编码环境值，统一从配置对象读取。
- **初始化可回放**：初始化关键节点要打结构化日志，方便排障。

## 5. 使用示例

### 5.1 定义配置对象

```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "muyi.demo")
public class DemoProperties {
    private boolean enabled = true;
    private int timeoutMs = 3000;
}
```

### 5.2 在初始化流程中使用配置

```java
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DemoInitConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DemoInitConfiguration.class);
    private final DemoProperties demoProperties;

    @PostConstruct
    public void init() {
        log.info("demo init enabled={}, timeoutMs={}", demoProperties.isEnabled(), demoProperties.getTimeoutMs());
    }
}
```

## 6. 常见问题

### 6.1 配置未生效

优先检查：

- 配置前缀是否与 `@ConfigurationProperties` 一致；
- 配置类是否被 Spring 扫描；
- 是否在多环境文件中被覆盖。

### 6.2 启动顺序导致初始化异常

若初始化依赖外部组件（数据库、注册中心等），建议将逻辑分阶段执行，并在日志中明确每阶段状态。
