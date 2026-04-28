# sso 模块使用说明

## 1. 模块定位

`tech-muyi-base-sso` 提供统一的登录态管理与多租户隔离能力，适用于网关后端、管理后台和多租户业务服务。

典型用途：

- 基于 token 的登录态校验
- 请求级用户上下文透传
- SQL 层租户数据自动隔离
- 可插拔的登录信息获取扩展

## 2. 核心能力

### 2.1 自动装配

当 `muyi.sso.enable=true` 时，模块会自动注册 `MySsoManager`，并启用 Web 拦截链中的 `SsoInterceptor`。

### 2.2 统一上下文管理

`MySsoManager` 提供当前请求登录态读写能力：

- `set(...)` / `remove()`：写入和清理线程上下文
- `getSsoInfo()` / `getSsoId()` / `getSsoName()`：读取当前用户信息
- `cache(...)` / `getCache(...)` / `cleanCache(...)`：读写 Redis token 缓存

### 2.3 多租户隔离

开启 `muyi.tenant.enable=true` 后，模块通过租户拦截器进行 SQL 自动追加租户条件，并支持：

- 忽略表（`ignoreTable`）
- 超级租户（`superTenantId`）
- 公共租户（`commonTenantId`）

### 2.4 登录信息扩展点

通过 `SsoInfoHook`、`SsoInfoHookFactory` 可按项目自定义 token 解析、用户信息加载策略。

## 3. 依赖接入

如果你已引入聚合模块（如 `tech-muyi-base-on`），通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-sso</artifactId>
</dependency>
```

## 4. 配置说明

### 4.1 application.yml 示例

```yaml
muyi:
  sso:
    enable: true
    tokenKey: my-sso:
    tag: token
    # MySsoInfo 子类（全限定类名）
    ssoInfoClass: tech.muyi.demo.sso.dto.CustomSsoInfo
    # token 默认有效期（秒）
    effectiveTime: 3600
    # 不需要登录拦截的接口
    exclude:
      - /actuator/**
      - /health
  tenant:
    enable: true
    tenantIdColumn: tenant_id
    ignoreTable:
      - sys_dict
    superTenantId:
      - SUPER
    commonTenantId:
      - COMMON
```

> 说明：`muyi.sso` 与 `muyi.tenant` 均支持 Spring Boot 宽松绑定写法（如 `tenant-id-column`）。

## 5. 使用示例

### 5.1 业务中读取当前登录信息

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.muyi.sso.MySsoManager;

@Service
public class CurrentUserService {

    @Autowired
    private MySsoManager mySsoManager;

    public String currentSsoId() {
        return mySsoManager.getSsoId();
    }
}
```

### 5.2 登录后写入缓存，退出时清理

```java
public void onLogin(MySsoInfo info) {
    mySsoManager.cache(info, info.getExpirationTime());
}

public void onLogout(String token) {
    mySsoManager.cleanCache(token);
}
```

### 5.3 扩展 `MySsoInfo` 字段（推荐）

`MySsoInfo` 支持使用业务子类扩展字段。  
在配置中声明 `muyi.sso.ssoInfoClass` 后，`MySsoManager` 读取缓存时会按该子类反序列化，业务侧可直接获取扩展字段。

```java
import lombok.Data;
import tech.muyi.sso.dto.MySsoInfo;

@Data
public class CustomSsoInfo extends MySsoInfo {
    private String deptId;
    private String roleCode;
}
```

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.muyi.sso.MySsoManager;

@Service
public class SsoExtFieldService {

    @Autowired
    private MySsoManager<CustomSsoInfo> mySsoManager;

    public String currentDeptId() {
        CustomSsoInfo ssoInfo = mySsoManager.getSsoInfo();
        return ssoInfo == null ? null : ssoInfo.getDeptId();
    }
}
```

## 6. 运维与接入建议

- **排除路径收敛**：`exclude` 只放基础接口，避免误放业务接口。
- **租户字段统一**：数据库字段命名与 `tenantIdColumn` 保持一致。
- **缓存过期对齐**：Redis 过期时间与 token 过期策略保持一致。
- **线程清理兜底**：异步场景注意在任务结束后清理上下文，避免串请求污染。

## 7. 常见问题

### 7.1 接口被误拦截

检查 `muyi.sso.exclude` 是否覆盖了健康检查、登录入口、静态资源路径。

### 7.2 租户数据串查

检查是否开启了 `muyi.tenant.enable`，并确认 SQL 对应表不在 `ignoreTable` 中。
