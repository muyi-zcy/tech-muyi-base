package tech.muyi.oss;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * 与具体实现无关的文件存储门面：上传、下载、删除。
 *
 * <p>在 {@code application.yml} 中设置 {@code muyi.file.storage-type} 为 {@code ftp} 或 {@code minio} 后，
 * 可仅注入本接口切换底层实现，无需改业务代码。对象键 {@code objectKey} 统一使用以 {@code /} 分隔的逻辑路径
 *（例如 {@code contract/2024/a.pdf}），由实现映射到 FTP 目录或 MinIO 对象名。</p>
 *
 * <p>若仍希望直接使用 FTP / MinIO 专有 API，可继续注入 {@link FtpTemplate} 或 {@link MinioTemplate}，
 * 并分别通过 {@code muyi.file.ftp.enable}、{@code muyi.file.minio.enable} 控制装配。</p>
 */
public interface FileStorageTemplate {

    String upload(MultipartFile file, String... path);

    String upload(String fileName, MultipartFile file, String... path);

    String upload(String fileName, File file, String... path);

    String upload(String fileName, InputStream is, String... path);

    /**
     * @param objectKey 逻辑对象键，使用 {@code /} 分隔，不含前导斜杠
     */
    InputStream download(String objectKey);

    /**
     * @param objectKey 逻辑对象键，使用 {@code /} 分隔，不含前导斜杠
     */
    void delete(String objectKey);
}
