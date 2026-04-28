package tech.muyi.oss.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * MinIO 存储是否启用：显式打开 {@code muyi.file.minio.enable}，或统一门面指定 {@code muyi.file.storage-type=minio}。
 */
public class MinioStorageEnabledCondition extends AnyNestedCondition {

    public MinioStorageEnabledCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(prefix = "muyi.file.minio", name = "enable", havingValue = "true")
    static class MinioEnableFlag {
    }

    @ConditionalOnProperty(name = "muyi.file.storage-type", havingValue = "minio")
    static class UnifiedStorageType {
    }
}
