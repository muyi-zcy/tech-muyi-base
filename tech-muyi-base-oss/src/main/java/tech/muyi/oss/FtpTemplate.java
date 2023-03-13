package tech.muyi.oss;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tomcat.util.collections.SynchronizedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.exception.MyException;
import tech.muyi.oss.exception.OssErrorCodeEnum;
import tech.muyi.oss.ftp.Ftp;
import tech.muyi.oss.ftp.FtpClientPool;
import tech.muyi.oss.properties.FtpProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author: muyi
 * @date: 2022/11/29
 **/
@Slf4j
@Configuration
@ConditionalOnClass(FTPClient.class)
@AutoConfigureBefore(FtpClientPool.class)
@EnableConfigurationProperties({FtpProperties.class})
@ConditionalOnProperty(name = {"muyi.file.ftp.enable"}, havingValue = "true")
@Import({FtpClientPool.class})
public class FtpTemplate {
    @Autowired
    private FtpClientPool ftpClientPool;
    private SynchronizedQueue<Ftp> ftpCache;
    @Autowired
    private FtpProperties ftpProperties;

    @PostConstruct
    public void init() {
        if (ftpProperties.isPoolCatch()) {
            this.ftpCache = new SynchronizedQueue<>(ftpProperties.getPoolCatchSize());
        }
    }

    private Ftp getFtp() {
        try {
            Ftp ftp = null;
            if (ftpProperties.isPoolCatch()) {
                ftp = ftpCache.poll();
            }
            FTPClient client = ftpClientPool.borrowObject();
            if (ftp == null) {
                ftp = new Ftp(ftpClientPool, client);
            } else {
                ftp.setClient(client);
            }
            ftp.setBackToPwd(Boolean.TRUE);
            return ftp;
        } catch (Exception e) {
            log.error("获取FTP连接失败！异常原因：", e);
            throw new MyException(OssErrorCodeEnum.FTP_CONN_ERROR, e);
        }
    }

    public String upload(String path, MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            return upload(path, originalFileName,file.getInputStream());
        } catch (IOException e) {
            log.error("FTP上传【{}】-【{}】失败！异常原因：{}", path, file, e);
            throw new MyException(OssErrorCodeEnum.FTP_UPLOAD_ERROR, e);
        }
    }

    public String upload(String path, File file) {
        try {
            return upload(path, file.getName(), Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            log.error("FTP上传【{}】-【{}】失败！异常原因：{}", path, file, e);
            throw new MyException(OssErrorCodeEnum.FTP_UPLOAD_ERROR, e);
        }
    }

    public String upload(String path, String fileName, InputStream is) {
        Ftp ftp = getFtp();
        try {
            return ftp.upload(path, fileName, is) ? ftpProperties.getUrlPrefix() + path + File.separator + fileName : null;
        } finally {
            ftp.close();
            if (ftpProperties.isPoolCatch()) {
                ftpCache.offer(ftp);
            }
            IoUtil.close(is);
        }
    }

    public InputStream download(String path, String fileName) {
        Ftp ftp = getFtp();
        try {
            return ftp.download(path, fileName);
        } catch (Exception e) {
            log.error("FTP下载【{}】-【{}】失败！异常原因：{}", path, fileName, e);
            throw new MyException(OssErrorCodeEnum.FTP_DOWNLOAD_ERROR, e);
        } finally {
            ftp.close();
            if (ftpProperties.isPoolCatch()) {
                ftpCache.offer(ftp);
            }
        }
    }

    public void deleteFile(String fileName) {
        Ftp ftp = getFtp();
        try {
            ftp.delFile(fileName);
        } catch (Exception e) {
            log.error("FTP删除【{}】失败！异常原因：{}", fileName, e);
            throw new MyException(OssErrorCodeEnum.FTP_DEL_ERROR, e);
        } finally {
            ftp.close();
            if (ftpProperties.isPoolCatch()) {
                ftpCache.offer(ftp);
            }
        }
    }
}
