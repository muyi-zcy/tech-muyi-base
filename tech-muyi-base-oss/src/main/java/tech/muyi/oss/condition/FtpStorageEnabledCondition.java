package tech.muyi.oss.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * FTP 存储是否启用：显式打开 {@code muyi.file.ftp.enable}，或统一门面指定 {@code muyi.file.storage-type=ftp}。
 */
public class FtpStorageEnabledCondition extends AnyNestedCondition {

    public FtpStorageEnabledCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(prefix = "muyi.file.ftp", name = "enable", havingValue = "true")
    static class FtpEnableFlag {
    }

    @ConditionalOnProperty(name = "muyi.file.storage-type", havingValue = "ftp")
    static class UnifiedStorageType {
    }
}
