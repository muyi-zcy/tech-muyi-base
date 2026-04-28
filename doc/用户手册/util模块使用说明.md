# util 模块使用说明

## 1. 模块定位

`tech-muyi-base-util` 提供跨模块复用的通用工具能力，重点用于降低重复代码、统一基础行为。

覆盖场景：

- JSON 序列化/反序列化；
- 日期时间处理与转换；
- 请求上下文、IP 与 Cookie 处理；
- Bean 映射与对象转换；
- Spring 容器静态取 Bean；
- 线程上下文 TTL 透传。

适用原则：

- 业务无关、跨模块复用的能力放入 util；
- 业务规则、业务策略不要沉淀到 util。

## 2. 模块能力地图

### 2.1 JSON 与序列化

- `MyJson`：基于 Gson 的统一入口。
  - `toJson(Object)` / `fromJson(String, Class<T>)`；
  - `fromJsonToList(String, Class<T>)`。
- `MyObjectMapper`：按“优先 Spring Bean、失败则默认 new ObjectMapper()”的策略获取 `ObjectMapper`。

`MyJson` 的默认策略：

- `LongSerializationPolicy.STRING`：long 以字符串输出，规避前端精度丢失；
- `serializeNulls()`：保留空字段，减少协议歧义；
- `disableHtmlEscaping()`：保持原文，不做 HTML 转义。

### 2.2 日期时间

- `DateUtils`：日期格式化、时间增减、时间差计算、类型转换等能力集合。
- 常用格式常量：`webFormat`、`newFormat`、`fullDateFormat` 等。

重点方法：

- 格式化：`formatDate(...)`、`formatByTimestamp(...)`；
- 计算：`addDays(...)`、`addHours(...)`、`addMinutes(...)`、`addSeconds(...)`；
- 差值：`getDiffMilliseconds(...)`、`getDiffSeconds(...)`、`getDiffMinutes(...)`、`getDiffDays(...)`；
- 类型转换：`toDate(...)`、`toTimestamp(...)`、`toTime(...)`、`toUtilDate(...)`、`toCalendar(...)`。

### 2.3 请求与网络

- `IpUtil`：
  - `getIpAddr(HttpServletRequest)`：按代理头优先级回退获取客户端 IP；
  - `getIP()`：获取当前机器首个可用 IPv4。
- `RequestUtil`：
  - `getHeaderMap(HttpServletRequest)`：提取请求 Header 快照。
- `CookieUtils`：
  - 读取 cookie：`getCookieValue(...)`；
  - 写入 cookie：`setCookie(...)`；
  - 删除 cookie：`deleteCookie(...)`。

### 2.4 对象映射

- `MapperUtils`（Orika）：
  - 默认同名字段映射：`map(...)`、`mapAsList(...)`；
  - 支持 `configMap` 自定义字段映射；
  - 内置 `JSONObjectConverter` 处理特定 JSON 类型转换。

### 2.5 容器与上下文

- `ApplicationContextUtil`：在非 Spring 托管对象中按需获取 Bean。
- TTL 相关：
  - `MyTransmittableThreadLocal<T>`：带 `code` 与 `classT` 的 TTL 封装；
  - `MyTtlContextManager`：注册、上下文下传、上下文恢复。

## 3. 依赖接入

如果已引入 `tech-muyi-base-on`，通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-util</artifactId>
</dependency>
```

## 4. 配置与约束

`util` 模块本身无强制 `application.yml` 配置项，但有以下行为约束：

- `MyObjectMapper` 在非 Spring 环境会回退默认实例，行为可能与线上统一配置不一致；
- `CookieUtils` 默认 path 为 `/`，且未统一设置 `HttpOnly/Secure/SameSite`；
- `MyTtlContextManager` 当前用普通 `HashMap` 存注册表，建议在启动期完成注册；
- `IpUtil` 依赖代理头，生产应配合可信代理策略。

## 5. 核心类详解与使用示例

### 5.1 `MyJson`：统一 JSON 入口

```java
import tech.muyi.util.MyJson;

String json = MyJson.toJson(java.util.Map.of("id", 123L, "name", "muyi"));
java.util.Map obj = MyJson.fromJson(json, java.util.Map.class);
java.util.List<String> names = MyJson.fromJsonToList("[\"a\",\"b\"]", String.class);
```

适用建议：

- 接口协议或日志输出优先走统一入口；
- 不在业务层混用多套 JSON 工具。

### 5.2 `DateUtils`：日期格式化与转换

```java
import tech.muyi.util.DateUtils;

String now = DateUtils.formatByTimestamp(System.currentTimeMillis(), DateUtils.newFormat);
java.util.Date after10Min = DateUtils.addMinutes(new java.util.Date(), 10);
java.sql.Timestamp ts = DateUtils.toTimestamp("2026-04-28T10:00:00");
```

注意事项：

- `convert(...)` 解析失败返回空串，不抛异常；
- 部分转换方法失败会抛 `RuntimeException`，调用侧需兜底；
- 解析格式要求较严格，输入需满足目标方法约定。

### 5.3 `MapperUtils`：对象映射

```java
import tech.muyi.util.bean.MapperUtils;

OrderDTO dto = MapperUtils.ORIKA.map(OrderDTO.class, orderDO);
java.util.List<OrderDTO> list = MapperUtils.ORIKA.mapAsList(OrderDTO.class, orderDOList);
```

自定义字段映射示例：

```java
java.util.Map<String, String> map = new java.util.HashMap<>();
map.put("sourceField", "targetField");
OrderDTO dto = MapperUtils.ORIKA.map(OrderDTO.class, orderDO, map);
```

### 5.4 `RequestUtil` / `IpUtil`：请求信息提取

```java
import tech.muyi.util.IpUtil;
import tech.muyi.util.request.RequestUtil;

String clientIp = IpUtil.getIpAddr(request);
java.util.Map<String, String> headers = RequestUtil.getHeaderMap(request);
```

### 5.5 `CookieUtils`：Cookie 读写

```java
import tech.muyi.util.request.CookieUtils;

CookieUtils.setCookie(request, response, "token", "abc123", 3600, true);
String token = CookieUtils.getCookieValue(request, "token", true);
CookieUtils.deleteCookie(request, response, "token");
```

### 5.6 TTL：线程上下文透传

```java
import tech.muyi.util.ttl.MyTransmittableThreadLocal;
import tech.muyi.util.ttl.MyTtlContextManager;

public class DemoContext {
    private static final MyTransmittableThreadLocal<String> REQUEST_ID_TTL =
            new MyTransmittableThreadLocal<>("requestId", String.class);

    public void process() {
        REQUEST_ID_TTL.set("req-123");
        java.util.Map<String, String> data = MyTtlContextManager.downAllData();
        MyTtlContextManager.upAllData(data);
    }
}
```

## 6. 最佳实践

- **单一入口**：同类能力统一到 util 入口，避免不同实现并存。
- **边界清晰**：util 只承载技术工具，不承载业务决策。
- **显式异常策略**：调用日期/转换工具时明确“失败返回默认值”还是“失败即报错”。
- **上下文最小化**：TTL 只透传必要字段，防止上下文污染。
- **安全优先**：Cookie 与 IP 相关能力结合网关/安全中间件统一治理。

## 7. 常见问题与排障

### 7.1 异步场景上下文丢失

排查顺序：

1. 是否使用 `MyTransmittableThreadLocal` 并完成注册；
2. 是否在任务提交前完成上下文写入；
3. 异步线程池是否启用了 `MyTaskDecorator`（见 core 模块文档）。

### 7.2 不同环境 JSON 行为不一致

常见原因：

- 一处用 `MyJson`，另一处直接 new 了 JSON 工具；
- `MyObjectMapper` 在部分链路回退成默认实例。

建议统一序列化入口，并确认 Spring 容器中的 `ObjectMapper` 配置。

### 7.3 Cookie 在子域不生效

优先检查：

- domain 推导是否符合当前域名结构；
- 是否需要显式设置安全属性（`HttpOnly`、`Secure`、`SameSite`）；
- 是否被浏览器策略或网关重写。

### 7.4 日期转换抛异常或返回空

优先核对输入格式与目标方法契约，例如：

- `toTimestamp(String)` 依赖 `LocalDateTime.parse` 默认格式；
- `toDate(String)` 依赖 `java.sql.Date.valueOf` 支持格式；
- `convert(...)` 失败返回空字符串。

## 8. 关联文档

- 基础导航：`doc/用户手册/基础能力模块手册.md`
- core 模块：`doc/用户手册/core模块使用说明.md`
- common 模块：`doc/用户手册/common模块使用说明.md`
