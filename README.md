# Tech-Muyi-Base

<div align="center">
  <h3>🚀 基于 Spring Cloud Alibaba 的企业级微服务开发脚手架</h3>
  <p>快速构建高效、可靠的分布式应用基础框架</p>
  
  ![Java](https://img.shields.io/badge/Java-8+-orange.svg)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.12-brightgreen.svg)
  ![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Hoxton.SR12-blue.svg)
  ![Spring Cloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2.2.7-red.svg)
  ![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)
</div>

## 📖 项目介绍

Tech-Muyi-Base 是一款基于 Spring Cloud Alibaba 架构的企业级后端开发脚手架，专为提升开发效率而设计。该脚手架集成了微服务开发中常用的组件和最佳实践，让开发者能够专注于业务逻辑实现，无需重复处理基础设施搭建。

### ✨ 特性

- 🏗️ **模块化设计** - 按需引入，灵活组合
- 🔧 **开箱即用** - 预配置常用组件，快速启动项目
- 📝 **完善文档** - 代码注释丰富，易于理解和扩展
- 🔒 **安全可靠** - 内置安全组件和最佳实践
- 🚀 **高性能** - 集成缓存、连接池等性能优化组件
- 🌐 **分布式友好** - 支持服务注册发现、配置中心等
- 📊 **可观测性** - 内置日志追踪、监控等功能

## 🛠️ 技术栈

### 核心框架
| 技术栈 | 版本 | 说明 |
|--------|------|------|
| Spring Boot | 2.3.12.RELEASE | 基础框架 |
| Spring Cloud | Hoxton.SR12 | 微服务框架 |
| Spring Cloud Alibaba | 2.2.7.RELEASE | 阿里巴巴微服务组件 |

### 数据层
| 技术栈 | 版本 | 说明 |
|--------|------|------|
| MySQL Connector | 8.0.17 | MySQL数据库驱动 |
| MyBatis Plus | 3.1.1 | ORM框架 |
| Druid | 1.1.10 | 数据库连接池 |
| Sharding-JDBC | 4.0.0 | 分库分表中间件 |

### 微服务组件
| 技术栈 | 版本 | 说明 |
|--------|------|------|
| Nacos | 跟随Spring Cloud Alibaba | 服务注册发现&配置中心 |
| Dubbo | 跟随Spring Cloud Alibaba | RPC框架 |
| Sentinel | 跟随Spring Cloud Alibaba | 流量控制&熔断降级 |

### 中间件
| 技术栈 | 版本 | 说明 |
|--------|------|------|
| RocketMQ Client | 4.7.0 | 消息队列客户端 |
| Redisson | 3.13.6 | Redis客户端 |
| MinIO | 7.0.2 | 对象存储服务 |
| Elastic Job | 2.1.5 | 分布式定时任务 |

### 工具库
| 技术栈 | 版本 | 说明 |
|--------|------|------|
| Hutool | 5.7.22 | Java工具类库 |
| Commons Lang3 | 3.12.0 | Apache工具类 |
| Transmittable ThreadLocal | 2.12.6 | 线程间上下文传递 |
| Lombok | 1.18.22 | 代码简化工具 |

### API文档
| 技术栈 | 版本 | 说明 |
|--------|------|------|
| Springfox | 3.0.0 | API文档生成 |
| Knife4j | 3.0.3 | API文档增强UI |

## 🏗️ 模块架构

```
tech-muyi-base
├── tech-muyi-base-dependencies    # 依赖管理父包
├── tech-muyi-base-boot           # 标准依赖父包
├── tech-muyi-base-core           # 核心模块
├── tech-muyi-base-common         # 公共模块
├── tech-muyi-base-util           # 工具模块
├── tech-muyi-base-config         # 配置模块
├── tech-muyi-base-exception      # 异常处理模块
├── tech-muyi-base-api            # API文档模块
├── tech-muyi-base-log            # 日志模块
├── tech-muyi-base-redis          # Redis集成
├── tech-muyi-base-id             # ID生成器
├── tech-muyi-base-sso            # 单点登录&多租户
├── tech-muyi-base-rpc            # RPC服务封装
├── tech-muyi-base-feign          # Feign集成
├── tech-muyi-base-mq             # 消息队列集成
├── tech-muyi-base-message        # 消息服务（邮件等）
├── tech-muyi-base-oss            # 对象存储集成
├── tech-muyi-base-sentinel       # 流量控制
├── tech-muyi-base-tracer         # 链路追踪
├── tech-muyi-base-okhttp         # HTTP客户端
├── tech-muyi-base-flyway         # 数据库版本控制
├── tech-muyi-base-dmask          # 数据脱敏
├── tech-muyi-base-db-shardingjdbc # 分库分表
├── tech-muyi-base-elasticjob     # 分布式定时任务
├── tech-muyi-base-easyjob        # 简单定时任务
├── tech-muyi-base-gengrator      # 代码生成器
├── tech-muyi-base-on             # 基础模块
└── my-log-plugins                # 日志插件集合
    ├── my-log-core               # 日志核心
    ├── my-log-springmvc-plugin   # SpringMVC日志插件
    ├── my-log-dubbo-plugin       # Dubbo日志插件
    ├── my-log-rocketmq-plugin    # RocketMQ日志插件
    ├── my-log-redis-plugin       # Redis日志插件
    ├── my-log-db-plugin          # 数据库日志插件
    └── my-log-okhttp-plugin      # OkHttp日志插件
```

### 模块说明

#### 🔧 基础模块（必选）
- **tech-muyi-base-core** - 核心功能模块
- **tech-muyi-base-common** - 公共组件和工具
- **tech-muyi-base-config** - 基础配置信息
- **tech-muyi-base-exception** - 统一异常处理
- **tech-muyi-base-util** - 工具类集合
- **tech-muyi-base-id** - 分布式ID生成器
- **tech-muyi-base-on** - 基础依赖模块

#### 🌐 微服务模块（可选）
- **tech-muyi-base-rpc** - RPC服务封装
- **tech-muyi-base-feign** - 服务间调用
- **tech-muyi-base-sso** - 单点登录和多租户解决方案
- **tech-muyi-base-sentinel** - 流量控制和熔断降级

#### 💾 数据存储模块（可选）
- **tech-muyi-base-redis** - Redis缓存集成
- **tech-muyi-base-db-shardingjdbc** - 分库分表支持
- **tech-muyi-base-flyway** - 数据库版本控制

#### 📡 消息通信模块（可选）
- **tech-muyi-base-mq** - RocketMQ消息队列
- **tech-muyi-base-message** - 消息服务（邮件等）
- **tech-muyi-base-okhttp** - HTTP客户端

#### 📋 任务调度模块（可选）
- **tech-muyi-base-elasticjob** - 分布式定时任务（依赖Zookeeper）
- **tech-muyi-base-easyjob** - 简单定时任务

#### 🔍 监控观测模块（可选）
- **tech-muyi-base-log** - 日志解决方案
- **tech-muyi-base-tracer** - 链路追踪
- **my-log-plugins** - 各组件日志插件

#### 🛠️ 开发工具模块（可选）
- **tech-muyi-base-api** - Swagger API文档
- **tech-muyi-base-gengrator** - 代码生成器
- **tech-muyi-base-oss** - 对象存储
- **tech-muyi-base-dmask** - 数据脱敏

## 🚀 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis 3.0+

### 使用方式

#### 1. 依赖引入

在您的项目 `pom.xml` 中引入：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-boot</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. 按需添加模块

根据业务需要添加额外模块：

```xml
<!-- Redis支持 -->
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-redis</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 消息队列支持 -->
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-mq</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 3. 配置文件

在 `application.yml` 中添加相应配置：

```yaml
spring:
  application:
    name: your-service-name
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_db
    username: your_username
    password: your_password
```

## 🏛️ 推荐项目结构

```
your-project/
├── src/main/java/com/yourcompany/project/
│   ├── client/          # 对外提供的客户端
│   ├── common/          # 公共代码
│   ├── core/            # 核心业务逻辑
│   ├── dependency/      # 其他服务依赖
│   ├── task/            # 定时任务、初始化任务
│   ├── test/            # 测试相关
│   └── web/             # Web控制器
├── src/main/resources/
│   ├── application.yml  # 配置文件
│   └── mapper/          # MyBatis映射文件
└── pom.xml
```

## 🔧 开发环境搭建

### Docker环境

推荐使用Docker快速搭建开发环境：

```bash
git clone https://github.com/muyi-zcy/InitDev_Docker
cd InitDev_Docker
docker-compose up -d
```

详细环境搭建文档：[InitDev_Docker](https://github.com/muyi-zcy/InitDev_Docker)

### 代码生成器

使用配套的代码生成器快速生成项目骨架：

```bash
# 使用Maven Archetype生成项目
mvn archetype:generate \
  -DgroupId=com.yourcompany \
  -DartifactId=your-project \
  -DarchetypeGroupId=tech.muyi \
  -DarchetypeArtifactId=tech-muyi-archetype
```

Archetype模板：[tech-muyi-archetype](https://github.com/muyi-zcy/archetype)

## 📚 示例项目

### 后端示例
完整的后端项目示例，展示如何使用本脚手架：
[tech-muyi-base-boot-sample](https://github.com/muyi-zcy/tech-muyi-base-boot-sample)

### 前端示例
基于React + Amis的管理后台示例：
[tech-muyi-base-boot-admin](https://github.com/muyi-zcy/tech-muyi-base-boot-admin)

## 📖 文档

### 版本兼容性

关于Spring Cloud Alibaba各组件版本兼容性，请参考官方文档：
[Spring Cloud Alibaba版本说明](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

### 路线图

- [ ] 基于xxl-job的定时任务支持
- [ ] Kubernetes部署支持
- [ ] 更多数据库支持（PostgreSQL、MongoDB等）
- [ ] 微服务治理增强
- [ ] 性能监控仪表板

## 🤝 贡献

欢迎提交Issue和Pull Request来帮助改进项目！

1. Fork本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 📄 许可证

本项目采用Apache License 2.0许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🔗 相关链接

- [项目主页](https://github.com/muyi-zcy/tech-muyi-base)
- [Docker环境](https://github.com/muyi-zcy/InitDev_Docker)
- [项目骨架](https://github.com/muyi-zcy/archetype)
- [后端示例](https://github.com/muyi-zcy/tech-muyi-base-boot-sample)
- [前端示例](https://github.com/muyi-zcy/tech-muyi-base-boot-admin)

---

<div align="center">
  <p>如果这个项目对您有帮助，请给它一个⭐️</p>
  <p>Made with ❤️ by Muyi</p>
</div>