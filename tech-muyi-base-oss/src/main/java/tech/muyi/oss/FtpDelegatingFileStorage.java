package tech.muyi.oss;

import org.springframework.web.multipart.MultipartFile;
import tech.muyi.oss.properties.FtpProperties;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FtpDelegatingFileStorage implements FileStorageTemplate {

    private final FtpTemplate ftpTemplate;
    private final FtpProperties ftpProperties;

    FtpDelegatingFileStorage(FtpTemplate ftpTemplate, FtpProperties ftpProperties) {
        this.ftpTemplate = ftpTemplate;
        this.ftpProperties = ftpProperties;
    }

    @Override
    public String upload(MultipartFile file, String... path) {
        return ftpTemplate.upload(file, path);
    }

    @Override
    public String upload(String fileName, MultipartFile file, String... path) {
        return ftpTemplate.upload(fileName, file, path);
    }

    @Override
    public String upload(String fileName, File file, String... path) {
        return ftpTemplate.upload(fileName, file, path);
    }

    @Override
    public String upload(String fileName, InputStream is, String... path) {
        return ftpTemplate.upload(fileName, is, path);
    }

    @Override
    public InputStream download(String objectKey) {
        Path full = resolveToPath(objectKey);
        Path parent = full.getParent();
        String dir = parent != null ? parent.toString() : ftpProperties.getPath();
        Path fileName = full.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("objectKey 无效: " + objectKey);
        }
        return ftpTemplate.download(dir, fileName.toString());
    }

    @Override
    public void delete(String objectKey) {
        ftpTemplate.deleteFile(resolveToPath(objectKey).toString());
    }

    private Path resolveToPath(String objectKey) {
        String normalized = normalizeObjectKey(objectKey);
        String[] parts = Arrays.stream(normalized.split("/"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        if (parts.length == 0) {
            throw new IllegalArgumentException("objectKey 不能为空");
        }
        return Paths.get(ftpProperties.getPath(), parts);
    }

    private static String normalizeObjectKey(String objectKey) {
        if (objectKey == null || objectKey.trim().isEmpty()) {
            throw new IllegalArgumentException("objectKey 不能为空");
        }
        String s = objectKey.trim().replace('\\', '/');
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        return s;
    }
}
