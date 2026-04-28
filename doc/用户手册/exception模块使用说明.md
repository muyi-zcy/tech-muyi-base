# exception 模块使用说明

## 1. 模块定位

`tech-muyi-base-exception` 提供统一异常与错误码治理能力，用于保证跨服务异常语义一致、错误码可追踪、排障信息可读。

主要能力：

- 业务异常模型定义；
- 系统兜底异常定义；
- 错误码协议与枚举实现规范；
- 错误码扫描与聚合查询。

## 2. 核心模型

### 2.1 异常类型

- `MyException`：业务异常基类，承载业务错误码与描述。
- `UnknownException`：未知异常兜底，防止系统异常直接泄露。

### 2.2 错误码协议

- `BaseErrorInfoInterface`：错误码统一接口；
- `CommonErrorCodeEnum`：系统公共错误码；
- `@ErrorCodeInfoAnno`：错误码分组与说明注解；
- `ErrorCodeHelper`：全量错误码扫描与聚合工具。

## 3. 核心方法

### 3.1 `ErrorCodeHelper.getAllErrorCode()`

作用：

- 扫描实现 `BaseErrorInfoInterface` 的枚举；
- 按 `@ErrorCodeInfoAnno` 聚合成结构化错误码列表；
- 支撑错误码文档输出、治理平台展示、排障检索。

## 4. 依赖接入

如果已引入 `tech-muyi-base-on`，通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-exception</artifactId>
</dependency>
```

## 5. 使用示例

### 5.1 定义业务错误码枚举

```java
import tech.muyi.exception.annotation.ErrorCodeInfoAnno;
import tech.muyi.exception.inf.BaseErrorInfoInterface;

@ErrorCodeInfoAnno(module = "ORDER", moduleName = "订单模块")
public enum OrderErrorCodeEnum implements BaseErrorInfoInterface {
    ORDER_PARAM_INVALID("ORDER_40001", "订单参数不合法"),
    ORDER_NOT_FOUND("ORDER_40401", "订单不存在");

    private final String resultCode;
    private final String resultMsg;

    OrderErrorCodeEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
```

### 5.2 抛出业务异常

```java
import tech.muyi.exception.MyException;

public void submitOrder(boolean invalid) {
    if (invalid) {
        throw new MyException(OrderErrorCodeEnum.ORDER_PARAM_INVALID);
    }
}
```

## 6. 错误码治理规范

- **唯一性**：一个错误码只表达一个语义，禁止一号多义。
- **分区管理**：按模块规划错误码区间，避免跨团队冲突。
- **可读性**：错误信息应服务排障，避免“操作失败”这类模糊文案。
- **稳定性**：发布后错误码尽量不变，更改需走兼容评审。

## 7. 常见问题

### 7.1 为什么线上看到大量 `UNKNOWN_EXCEPTION`

通常是业务直接抛了非 `MyException` 的运行时异常。  
建议在业务边界统一转换为 `MyException`，并补充明确错误码。

### 7.2 错误码扫描未包含新枚举

请检查：

- 枚举是否实现 `BaseErrorInfoInterface`；
- 是否加了 `@ErrorCodeInfoAnno`；
- 是否在 Spring 扫描范围内。
