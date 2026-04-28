# Tech-Muyi-Base 使用手册

本目录提供 `tech-muyi-base` 的详细使用文档，按照模块能力分组维护，便于团队按需查阅与落地。

## 文档导航

- `doc/01-快速开始与接入指南.md`：环境要求、依赖引入、启动流程、接入建议。
- `doc/02-基础能力模块手册.md`：核心基础模块（core/common/config/util/exception/id/on）。
- `doc/03-数据与存储模块手册.md`：Redis、Flyway、Sharding-JDBC。
- `doc/04-微服务与通信模块手册.md`：RPC、Feign、MQ、Message、SSO、Sentinel、OkHttp。
- `doc/05-任务与调度模块手册.md`：EasyJob、ElasticJob。
- `doc/06-日志链路与观测模块手册.md`：Log、Tracer、my-log-plugins。
- `doc/07-开发辅助模块手册.md`：API、Generator、DMask、Boot、Dependencies。
- `doc/08-代码生成器使用说明.md`：生成器界面参数、按钮行为、生成流程、覆盖策略、排错。
- `doc/09-代码层级结构规范.md`：分层边界、调用方向、禁止项（跨表、写 SQL、同层调用等）。
- `doc/10-id模块使用说明.md`：ID 模块的接入、配置、示例与常见问题。
- `doc/11-sso模块使用说明.md`：SSO 鉴权、多租户配置、拦截流程与扩展点。
- `doc/12-oss模块使用说明.md`：MinIO/FTP 配置、`storage-type` 与 `FileStorageTemplate` 统一门面、上传下载示例与运维建议。

## 建议阅读顺序

1. 新项目首次接入：先读 `01`、`02`。
2. 需要中间件能力：按业务选择 `03`、`04`、`05`。
3. 需要排查问题与可观测能力：重点看 `06`。
4. 需要提效与规范化输出：看 `07`。
5. 使用代码生成器和团队规范落地：看 `08`、`09`。
6. 需要深入使用 ID 生成能力：看 `10`。
7. 需要登录态与租户隔离能力：看 `11`。
8. 需要文件存储与访问能力：看 `12`。

## 文档维护约定

- 新增模块时，同步更新本索引与对应分组文档。
- 配置项变更需补充默认值、是否必填、示例值、影响范围。
- 示例代码优先使用可直接复制的 `application.yml` 片段和 Java 最小调用样例。
