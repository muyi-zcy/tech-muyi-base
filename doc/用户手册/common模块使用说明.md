# common 模块使用说明

## 1. 模块定位

`tech-muyi-base-common` 提供跨服务复用的基础领域模型，重点覆盖三类能力：

- 统一接口返回模型（`MyResult`）
- 通用分页查询参数（`MyBaseQuery`）
- 基础 DO/DTO 字段与公共枚举协议（`MyBaseDO`、`MyBaseDTO`、`CommonEnum`）

适用于所有需要统一返回协议、分页约定、枚举语义一致性的业务服务。

> 本文档是 common 模块的单独详细说明；`doc/用户手册/基础能力模块手册.md` 中 common 章节仅保留概览与导航。

## 2. 核心能力

### 2.1 统一返回结构 `MyResult`

`MyResult<T>` 统一了 Controller 层输出结构，字段包含：

- `success`：业务是否成功
- `data`：业务数据
- `query`：分页/查询元信息（`MyBaseQuery`）
- `code`、`status`、`message`：错误码与错误描述
- `requestId`：请求链路标识（由上层按需透传）

约定：

- 成功默认 `code="0"`、`status=0`
- 失败优先按业务错误码返回
- 非 `MyException` 异常会降级为 `UNKNOWN_EXCEPTION`

### 2.2 分页查询基类 `MyBaseQuery`

`MyBaseQuery` 统一分页请求与响应元信息：

- `size`：分页大小（默认 `20`，最大 `2000`）
- `current`：页码（从 `1` 开始）
- `total`：总记录数（由后端回填）
- `getPageTotal()`：根据 `total/size` 计算总页数
- `getOffset()`：计算 SQL `offset`

这样可以减少各模块分页参数重复定义，并避免超大分页参数导致数据库压力问题。

### 2.3 基础实体模型 `MyBaseDO`、`MyBaseDTO`

`MyBaseDO` 提供数据库实体公共字段：

- `id`、`gmtCreate`、`gmtModified`
- `rowVersion`（乐观锁）
- `rowStatus`（逻辑删除状态，`0` 正常，`-1` 删除）
- `creator`、`operator`
- `tenantId`、`extAtt`

`MyBaseDTO` 提供接口传输公共字段，并通过 `@Operator` 支持操作者信息序列化扩展。

### 2.4 公共枚举协议 `CommonEnum` + `EnumCache`

`CommonEnum<T>` 统一枚举对外结构：

- `getCode()`：稳定协议值
- `getName()`：展示名
- `@JsonValue` 输出 `{code,name}` 结构（`CommonEnumJson`）

`EnumCache` 提供按 `name`/`value` 的高效反查机制，常见枚举（如 `BooleanEnum`、`ConditionEnum`、`RowStatusEnum`）通过 static 块预注册。

## 3. 依赖接入

如果你已引入聚合模块（如 `tech-muyi-base-on`），通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-common</artifactId>
</dependency>
```

## 4. 配置说明

`common` 模块本身**没有强制的 application.yml 配置项**，属于“开箱即用”基础模型层。  
业务侧主要通过：

- 统一返回 `MyResult`
- 查询入参继承 `MyBaseQuery`
- 枚举实现 `CommonEnum`

来完成规范落地。

## 5. 使用示例

### 5.1 Controller 统一返回 `MyResult`

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.muyi.common.MyResult;

@RestController
public class HealthController {

    @GetMapping("/health")
    public MyResult<String> health() {
        return MyResult.ok("ok");
    }
}
```

### 5.2 分页接口返回数据与分页元信息

```java
import tech.muyi.common.MyResult;
import tech.muyi.common.query.MyBaseQuery;

public MyResult<java.util.List<String>> list(MyBaseQuery query) {
    query.setTotal(105L);
    return MyResult.ok(java.util.Arrays.asList("A", "B"), query);
}
```

### 5.3 统一异常转换为失败响应

```java
import tech.muyi.common.MyResult;
import tech.muyi.exception.MyException;

public MyResult<Void> submit() {
    try {
        // do business
        return MyResult.ok();
    } catch (MyException e) {
        return MyResult.fail(e);
    } catch (Exception e) {
        return MyResult.fail(e);
    }
}
```

### 5.4 业务枚举实现 `CommonEnum`

```java
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import tech.muyi.common.constant.enumtype.CommonEnum;
import tech.muyi.common.constant.enumtype.EnumCache;

@Getter
public enum OrderStatusEnum implements CommonEnum<Integer> {
    NEW(1, "新建"),
    FINISHED(2, "已完成");

    @EnumValue
    private final Integer code;
    private final String name;

    OrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    static {
        EnumCache.registerByName(OrderStatusEnum.class, OrderStatusEnum.values());
        EnumCache.registerByValue(OrderStatusEnum.class, OrderStatusEnum.values(), OrderStatusEnum::getCode);
    }
}
```

## 6. 扩展点：`@Operator` 与 `IOperatorHandler`

`MyBaseDTO` 中的 `creator`、`operator` 使用了 `@Operator` 注解。  
当 Spring 容器中存在 `IOperatorHandler` Bean 时，序列化会自动调用：

- `convertOperator(Object operator)`：把操作人标识转换成可读信息（如“张三(1001)”）

如果未实现 `IOperatorHandler`，则保持原值输出，不影响接口可用性。

## 7. 落地建议

- **接口层统一**：Controller 返回值统一 `MyResult<?>`，避免多套返回协议并存。
- **分页防护**：请求参数统一继承 `MyBaseQuery`，复用分页上限保护。
- **枚举稳定性**：对外传输依赖 `code`，避免直接依赖枚举 `name()`。
- **实体继承规范**：DO 继承 `MyBaseDO`，DTO 继承 `MyBaseDTO`，减少重复字段。
- **异常分层**：业务异常优先抛 `MyException`，保证错误码可控。

## 8. 常见问题

### 8.1 `MyResult.fail(Throwable)` 返回的 `status` 不是业务码

当错误码无法转为数字时，`status` 会降级为 `UNKNOWN_EXCEPTION` 对应值。  
建议业务异常统一使用 `MyException` 并提供可解析的错误码字符串。

### 8.2 分页参数没传时默认值是多少

- `size` 默认 `20`
- `current` 默认 `1`
- `size` 超过上限时会被截断到 `2000`

### 8.3 枚举反查时报“未注册到枚举缓存”

请在枚举 `static` 块中注册：

- `EnumCache.registerByName(...)`
- `EnumCache.registerByValue(...)`

否则 `EnumCache.findByName/findByValue` 无法命中缓存。
