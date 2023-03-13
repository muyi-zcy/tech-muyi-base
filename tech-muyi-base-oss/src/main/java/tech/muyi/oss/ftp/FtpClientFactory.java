package tech.muyi.oss.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tech.muyi.exception.MyException;
import tech.muyi.oss.exception.OssErrorCodeEnum;
import tech.muyi.oss.properties.FtpProperties;

import java.io.IOException;
/**
 * @author: muyi
 * @date: 2022/11/29
 **/
@Slf4j
@Configuration
@ConditionalOnClass(FTPClient.class)
@EnableConfigurationProperties({FtpProperties.class})
@ConditionalOnProperty(name = {"muyi.file.ftp.enable"}, havingValue = "true")
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

    @Autowired
    private FtpProperties ftpProperties;

    @Override
    public FTPClient create() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(ftpProperties.getEncoding());
        ftpClient.setConnectTimeout(ftpProperties.getConnectTimeout());
        try {
            ftpClient.connect(ftpProperties.getEndpoint(), ftpProperties.getPort());
            ftpClient.login(ftpProperties.getName(), ftpProperties.getPassword());
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_CONN_ERROR,e);
        }
        // 是否成功登录服务器
        final int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new MyException(OssErrorCodeEnum.FTP_LOGIN_ERROR, ftpProperties.getName());
            }
            throw new MyException(OssErrorCodeEnum.FTP_LOGIN_ERROR, ftpProperties.getName());
        }
        ftpClient.setBufferSize(ftpProperties.getBufferSize());
        if (ftpProperties.isPassiveMode()) {
            ftpClient.enterLocalPassiveMode();
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient obj) {
        return new DefaultPooledObject<>(obj);
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }
        FTPClient ftpClient = ftpPooled.getObject();

        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_LOGOUT_ERROR,e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new MyException(OssErrorCodeEnum.FTP_DISCONN_ERROR,e);
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> ftpPooled) {
        try {
            FTPClient ftpClient = ftpPooled.getObject();
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            log.error("Failed to validate client:",e);
        }
        return false;
    }
}