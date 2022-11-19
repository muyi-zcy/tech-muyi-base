# 说明&介绍

tech-muyi-base是一个基于SpringCloudAlibaba编写的后端脚手架，便于开发人员免于考虑项目搭建之初繁琐的配置和提供常用的基础开发工具，代码简洁，注释丰富，上手容易。

# 技术栈

| 技术栈                     | 版本                   | 说明             |
| -------------------------- | ---------------------- | ---------------- |
| SpringBoot                 | 2.3.12.RELEASE         |                  |
| SpringCloud                | Hoxton.SR12            |                  |
| SpringCloudAlibaba         | 2.2.7.RELEASE          |                  |
| mysql-connector-java       | 8.0.17                 |                  |
| mybatis-plus               | 3.1.1                  |                  |
| druid                      | 1.1.10                 |                  |
| sharding-jdbc              | 4.0.0                  |                  |
| nacos                      | 跟随SpringCloudAlibaba |                  |
| dubbo                      | 跟随SpringCloudAlibaba |                  |
| rocketmq-client            | 4.7.0                  | RocketMQ客户端   |
| redisson                   | 3.13.6                 | redis客户端      |
| minio                      | 7.0.2                  | 文件存储服务     |
| hutool                     | 5.7.22                 | 超好用的工具类   |
| commons-lang3              | 3.12.0                 |                  |
| commons-beanutils          | 1.9.4                  |                  |
| transmittable-thread-local | 2.12.6                 | 线程间上下文传递 |
| slf4j                      | 1.7.21                 |                  |
| logback                    | 1.2.3                  |                  |
| lombok                     | 1.18.22                |                  |
| elastic-job                | 2.1.5                  | 分布式定时任务   |
| springfox                  | 3.0.0                  | api              |
| knife4j                    | 3.0.3                  |                  |
| fastjson                   | 2.0.2                  |                  |

> 其中关于使用对SpringCloud Alibaba适配各组件版本的使用，参考ali提供的参考文档：
> [https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)
> 
> 基于xxl-job的定时任务已在计划中


## 模块介绍

-  tech-muyi-base-oss：对象存储解决方案，需要额外引入
-  tech-muyi-base-exception ：异常处理，基础模块
-  tech-muyi-base-redis ：redis客户端，基础模块
-  tech-muyi-base-job-elastic ：使用elastic-job封装的定时任务，需要额外引入，依赖zookeeper
-  tech-muyi-base-job-easy ：使用hutool封装的简单定时任务，需要额外引入
-  tech-muyi-base-on ：基础模块
-  tech-muyi-base-dependencies ：向外提供的依赖父包
-  tech-muyi-base-log ：日志解决方案和rest的拦截打印
-  tech-muyi-base-api ：封装的swagger配置
-  tech-muyi-base-common ：公共模块，包含一些基础类和补充功能（例如限流，后续会单独封装）
-  tech-muyi-base-util ：工具模块，包含雪花算法、日期、json、freemark等等工具类，也引入了hutool等优秀的三方工具类，后续也会不断丰富
-  tech-muyi-base-db ：数据库模块
-  tech-muyi-base-mq ：mq模块，封装了rockermq的客户端，分为生产者端和消费者端，需要额外引入
-  tech-muyi-base-auth ：为服务鉴权sdk预留，后续会开发
-  tech-muyi-base-message ：消息模块，暂时只封装邮箱客户端（因为邮箱是免费的）
-  tech-muyi-base-mq/tech-muyi-base-mq-client
-  tech-muyi-base-mq/tech-muyi-base-mq-server
-  tech-muyi-base-rpc ：封装的rpc服务
-  tech-muyi-base-gengrator ：为tech-muyi-base二次封装的代码生成器，搭配archetype生成代码，直接起飞！

## 运行环境搭建

暂时使用docker镜像搭建运行环境，后续使用k8s进行管理
[https://github.com/muyi-zcy/InitDev_Docker](https://github.com/muyi-zcy/InitDev_Docker)

## Archetype骨架

[https://github.com/muyi-zcy/archetype](https://github.com/muyi-zcy/archetype)

## 项目结构

- client：本服务向外提供客户端
- common：公共部分
- core：服务核心逻辑
- dependency：其他服务依赖
- task：定时任务、初始化任务等
- test：测试相关
- web：服务接口

