# core 模块使用说明

## 1. 模块定位

`tech-muyi-base-core` 提供服务运行所需的核心基础设施，重点覆盖：

- 统一异常处理入口；
- 异步线程池与上下文透传；
- MyBatis Plus 分页能力；
- JSON 序列化与反序列化策略；
- DAO/Service 常用基础查询能力。

适用于所有需要快速具备“可运行、可观测、可治理”基础能力的后端服务。

## 2. 核心能力清单

### 2.1 全局异常处理

- `GlobalExceptionHandle.handleMyException(MyException ex)`：业务异常统一转换为失败响应。
- `GlobalExceptionHandle.handleThrowable(Throwable t)`：未知异常统一兜底，避免裸异常泄露到接口层。

### 2.2 异步执行与上下文透传

- `AsyncConfiguration`：统一线程池装配入口。
- `MyTaskDecorator.decorate(Runnable runnable)`：任务执行前后绑定并清理上下文，防止线程复用导致上下文串扰。

### 2.3 数据分页与序列化

- `PageConfig`：统一开启 MyBatis Plus 分页能力。
- `JacksonConfig`：统一 ObjectMapper 行为。
- `EnumDeserializer`：统一枚举反序列化策略，减少接口入参枚举转换不一致问题。

## 3. 依赖接入

如果已引入 `tech-muyi-base-on`，通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-core</artifactId>
</dependency>
```

## 4. 配置建议

`core` 模块以自动配置为主，没有强制业务配置项。建议重点关注：

- 线程池参数（核心线程数、最大线程数、队列容量）需结合业务峰值设置；
- 序列化策略尽量统一，不要在业务模块重复定义不同的 `ObjectMapper`；
- 分页插件启用后，统一使用分页对象，避免手写分页参数导致行为不一致。

## 5. 使用示例

### 5.1 线程池接入上下文透传

```java
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tech.muyi.core.config.task.MyTaskDecorator;

@Bean("bizExecutor")
public ThreadPoolTaskExecutor bizExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(16);
    executor.setQueueCapacity(200);
    executor.setTaskDecorator(new MyTaskDecorator());
    executor.initialize();
    return executor;
}
```

### 5.2 业务异常统一由全局处理器收敛

```java
import tech.muyi.exception.MyException;

public void createOrder() {
    if (invalid()) {
        throw new MyException("ORDER_PARAM_INVALID", "订单参数不合法");
    }
}
```

当异常抛出后，由 `GlobalExceptionHandle` 统一转换为标准失败响应，Controller 无需重复 try/catch。

## 6. 落地建议

- **异常治理**：业务异常必须抛 `MyException`，便于错误码治理与可观测分析。
- **线程池规范**：禁止直接使用 `new Thread(...)` 执行业务任务，统一接入线程池。
- **上下文安全**：异步场景必须启用 `MyTaskDecorator`，避免 traceId、租户信息丢失。
- **配置收敛**：跨服务的序列化与分页策略放到 core 层，不在业务服务重复定义。

## 7. 常见问题

### 7.1 异步任务中拿不到请求上下文

通常是线程池没有配置 `MyTaskDecorator`。  
请确认业务线程池均由统一配置创建，并绑定装饰器。

### 7.2 出现重复的全局异常处理行为

可能是业务服务又注册了额外的 `@ControllerAdvice`，并且与 core 中处理逻辑冲突。  
建议保留一个主处理器，扩展场景以“补充处理”为主。
