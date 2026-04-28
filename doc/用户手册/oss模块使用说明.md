# oss 模块使用说明

## 1. 模块定位

`tech-muyi-base-oss` 提供统一的文件存储访问能力，当前支持两类存储实现：

- MinIO（对象存储）
- FTP（文件服务器）

适用于图片、附件、导入导出文件等场景。

> 本文档是 OSS 能力的唯一维护入口；`doc/用户手册/数据与存储模块手册.md` 仅保留到本文件的导航引用。

## 2. 核心能力

### 2.1 两种接入方式

**方式一：按实现类型注入（强类型，适合需要 MinIO 专有能力的场景）**

- 注入 `MinioTemplate` 或 `FtpTemplate`，直接调用各自 API（例如 MinIO 的桶管理、预签名 URL）。
- 装配条件见下文「启用规则」。

**方式二：与实现无关的统一门面（推荐：切换存储只改配置）**

- 在 `application.yml` 中设置 **`muyi.file.storage-type`** 为 **`ftp`** 或 **`minio`**。
- 业务侧只注入 **`FileStorageTemplate`**（Spring Bean 名 **`fileStorageTemplate`**），上传/下载/删除走同一套接口。
- 切换底层实现时，一般只需改 **`storage-type`** 以及对应 **`muyi.file.minio` / `muyi.file.ftp`** 配置块，**无需改 Java 代码**。

### 2.2 启用规则（`enable` 与 `storage-type`）

以下满足**任一**条件即会装配对应后端（连接池、Template 等）：

| 后端 | 条件 |
|------|------|
| FTP | `muyi.file.ftp.enable=true`，**或** `muyi.file.storage-type=ftp` |
| MinIO | `muyi.file.minio.enable=true`，**或** `muyi.file.storage-type=minio` |

**`FileStorageTemplate` 的注册**：仅当配置了 **`muyi.file.storage-type`** 且取值为 **`ftp`** 或 **`minio`** 时，才会注册名为 `fileStorageTemplate` 的 Bean；未配置 `storage-type` 时不会注册统一门面，可避免与「只启用某一端做迁移/双写」等场景误绑。

**注意**：若同时 `muyi.file.ftp.enable=true` 与 `muyi.file.minio.enable=true`，两个 `*Template` 可能同时存在；此时请业务侧明确注入类型，或通过 **`storage-type`** 只启用统一门面并约定单一主存储。

### 2.3 `FileStorageTemplate` 与对象键 `objectKey`

统一门面使用**逻辑对象键** **`objectKey`**：

- 使用 **`/`** 分隔路径段，**不要**前导斜杠，例如：`invoice/2026/04/report.pdf`。
- **FTP**：在配置的 **`muyi.file.ftp.path`** 下拼接该键对应的路径；下载、删除时按键解析为「目录 + 文件名」。
- **MinIO**：使用配置中的 **`muyi.file.minio.bucket`**，将规范化后的 `objectKey` 作为对象名（与控制台中「文件夹」风格一致）。

门面方法概要：

- 上传：`upload(MultipartFile, String... path)` 及重载（与 `FtpTemplate` 分段路径语义一致；MinIO 侧委托 `uploadObject`，桶固定为配置项）。
- 下载：`download(String objectKey)`。
- 删除：`delete(String objectKey)`。

需要 **MinIO 桶列表、预签名 URL** 等能力时，仍请注入 **`MinioTemplate`**。

### 2.4 MinIO 能力（`MinioTemplate`）

`MinioTemplate` 提供桶管理与对象操作能力：

- 桶管理：`existBucket`、`makeBucket`、`listBuckets`、`removeBucket`
- 对象上传：`uploadObject(...)`（支持 `InputStream`、`File`、`MultipartFile`）
- 对象访问：`downloadObject`、`presignedGetObject`、`statObject`
- 对象删除：`deleteObject`

### 2.5 FTP 能力（`FtpTemplate`）

`FtpTemplate` 提供文件读写与连接池复用能力：

- 上传：`upload(...)`
- 下载：`download(目录路径, 文件名)`（与门面 `objectKey` 拆分方式不同，为历史 API）
- 删除：`deleteFile(...)`（参数可为含路径的完整文件路径字符串）

### 2.6 统一门面实现说明（最新）

- `MinioFileStorageConfiguration` 与 `FtpFileStorageConfiguration` 仅负责条件装配与 `fileStorageTemplate` Bean 注册。
- 统一门面的具体逻辑已拆分到独立类：
  - `MinioDelegatingFileStorage`
  - `FtpDelegatingFileStorage`
- 两个配置类内不再使用静态内部类，便于维护与排错。
- `FtpDelegatingFileStorage`、`MinioDelegatingFileStorage` 与 `FileStorageTemplate` 位于同一包 `tech.muyi.oss`，因此源码中可直接使用接口名，不需要额外 `import`（Java 同包可见性规则）。

## 3. 依赖接入

如果你已引入聚合模块（如 `tech-muyi-base-on`），通常无需单独引入。  
需要显式引入时：

```xml
<dependency>
    <groupId>tech.muyi</groupId>
    <artifactId>tech-muyi-base-oss</artifactId>
</dependency>
```

## 4. 配置说明

### 4.1 统一门面：按 `storage-type` 切换

**MinIO 示例（仅依赖 `storage-type` 即可装配 MinIO 与门面）：**

```yaml
muyi:
  file:
    storage-type: minio
    minio:
      endpoint: http://127.0.0.1
      port: 9000
      accessKey: minioadmin
      secretKey: minioadmin
      bucket: demo-bucket
      connectTimeout: 900
      maxActive: 10
      keepAliveDuration: 5
```

**FTP 示例：**

```yaml
muyi:
  file:
    storage-type: ftp
    ftp:
      endpoint: 127.0.0.1
      port: 21
      name: ftp-user
      password: ftp-pass
      urlPrefix: https://file.example.com
      path: /upload
      passiveMode: true
      connectTimeout: 30000
      maxActive: 8
      poolCatch: true
      poolCatchSize: 10
      keepAlive: true
      keepAliveCheckTime: 60000
```

仍可使用 **`enable: true`** 单独打开某一实现而不注册门面（不配 `storage-type` 即可），与历史行为一致。

### 4.2 MinIO 配置示例（仅强类型、不配 `storage-type`）

```yaml
muyi:
  file:
    minio:
      enable: true
      endpoint: http://127.0.0.1
      port: 9000
      accessKey: minioadmin
      secretKey: minioadmin
      bucket: demo-bucket
      connectTimeout: 900
      maxActive: 10
      keepAliveDuration: 5
```

### 4.3 FTP 配置示例（仅强类型、不配 `storage-type`）

```yaml
muyi:
  file:
    ftp:
      enable: true
      endpoint: 127.0.0.1
      port: 21
      name: ftp-user
      password: ftp-pass
      urlPrefix: https://file.example.com
      path: /upload
      passiveMode: true
      connectTimeout: 30000
      maxActive: 8
      poolCatch: true
      poolCatchSize: 10
      keepAlive: true
      keepAliveCheckTime: 60000
```

## 5. 使用示例

### 5.1 统一门面：上传、下载、删除（与实现无关）

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.oss.FileStorageTemplate;

@Service
public class AttachmentService {

    @Autowired
    private FileStorageTemplate fileStorage;

    public String save(MultipartFile file) {
        return fileStorage.upload(file, "invoice", "2026", "04");
    }

    public java.io.InputStream load(String objectKey) {
        return fileStorage.download(objectKey);
    }

    public void remove(String objectKey) {
        fileStorage.delete(objectKey);
    }
}
```

其中 `objectKey` 需与上传后的逻辑路径一致（例如上传时 path 为 `invoice/2026/04`、文件名为 `a.pdf`，则键为 `invoice/2026/04/a.pdf`）。

### 5.2 MinIO 上传并返回访问地址（强类型）

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.oss.MinioTemplate;

@Service
public class FileUploadService {

    @Autowired
    private MinioTemplate minioTemplate;

    public String uploadAvatar(MultipartFile file, String userId) {
        return minioTemplate.uploadObject("demo-bucket", "avatar-" + userId, file, "user", userId);
    }
}
```

### 5.3 生成短期签名下载地址（强类型）

```java
public String downloadUrl(String objectName) {
    return minioTemplate.presignedGetObject("demo-bucket", objectName, 300);
}
```

### 5.4 FTP 上传文件（强类型）

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.oss.FtpTemplate;

@Service
public class FtpInvoiceService {

    @Autowired
    private FtpTemplate ftpTemplate;

    public String uploadToFtp(MultipartFile file) {
        return ftpTemplate.upload(file, "invoice", "2026", "04");
    }
}
```

## 6. 运维与接入建议

- **路径规范**：按业务域 + 日期分层路径，便于生命周期管理与检索；使用门面时与 `objectKey` 保持一致。
- **访问控制**：外网访问优先使用签名 URL，避免直接暴露私有桶对象。
- **超时和重试**：大文件上传建议在业务层增加重试与幂等控制。
- **存储隔离**：生产、测试环境使用独立 bucket 或目录前缀。

## 7. 常见问题

### 7.1 上传后无法访问

检查对象路径是否正确、bucket 权限是否允许访问，或改为 `presignedGetObject` 下发签名链接。

### 7.2 FTP 连接不足或阻塞

检查 `maxActive`、`poolCatch` 配置，并确认调用链在异常分支也能正确归还连接。

### 7.3 配置了 `storage-type` 但没有 `fileStorageTemplate`

确认取值为小写 **`ftp`** 或 **`minio`**（与 Spring `havingValue` 一致），且 classpath 中存在对应依赖（如 MinIO 客户端、commons-net）。

### 7.4 只想用 MinIO 桶能力，不想注册门面

不要配置 **`muyi.file.storage-type`**，仅设置 **`muyi.file.minio.enable=true`** 并注入 **`MinioTemplate`** 即可。
