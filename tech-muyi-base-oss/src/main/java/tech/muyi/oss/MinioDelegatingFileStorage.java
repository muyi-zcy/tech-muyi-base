package tech.muyi.oss;

import org.springframework.web.multipart.MultipartFile;
import tech.muyi.oss.properties.MinioProperties;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MinioDelegatingFileStorage implements FileStorageTemplate {

    private final MinioTemplate minioTemplate;
    private final MinioProperties minioProperties;

    MinioDelegatingFileStorage(MinioTemplate minioTemplate, MinioProperties minioProperties) {
        this.minioTemplate = minioTemplate;
        this.minioProperties = minioProperties;
    }

    private String bucket() {
        return minioProperties.getBucket();
    }

    @Override
    public String upload(MultipartFile file, String... path) {
        return minioTemplate.uploadObject(bucket(), file.getOriginalFilename(), file, path);
    }

    @Override
    public String upload(String fileName, MultipartFile file, String... path) {
        return minioTemplate.uploadObject(bucket(), fileName, file, path);
    }

    @Override
    public String upload(String fileName, File file, String... path) {
        return minioTemplate.uploadObject(bucket(), fileName, file, path);
    }

    @Override
    public String upload(String fileName, InputStream is, String... path) {
        return minioTemplate.uploadObject(bucket(), fileName, is, path);
    }

    @Override
    public InputStream download(String objectKey) {
        return minioTemplate.downloadObject(bucket(), normalizeObjectKey(objectKey));
    }

    @Override
    public void delete(String objectKey) {
        minioTemplate.deleteObject(bucket(), normalizeObjectKey(objectKey));
    }

    private static String normalizeObjectKey(String objectKey) {
        if (objectKey == null || objectKey.trim().isEmpty()) {
            throw new IllegalArgumentException("objectKey 不能为空");
        }
        String s = objectKey.trim().replace('\\', '/');
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        return Arrays.stream(s.split("/"))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("/"));
    }
}
