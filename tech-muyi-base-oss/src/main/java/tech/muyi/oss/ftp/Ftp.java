package tech.muyi.oss.ftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import tech.muyi.exception.MyException;
import tech.muyi.oss.exception.OssErrorCodeEnum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: muyi
 * @date: 2022/11/29
 **/
@Slf4j
@ConditionalOnClass(FTPClient.class)
public class Ftp {

    private FTPClient client;

    private FtpClientPool ftpClientPool;

    public Ftp(FtpClientPool ftpClientPool, FTPClient client) {
        this.ftpClientPool = ftpClientPool;
        this.client = client;
    }
    private boolean backToPwd;

    public Ftp setBackToPwd(boolean backToPwd) {
        this.backToPwd = backToPwd;
        return this;
    }

    public boolean cd(String directory) {
        if (StringUtils.isEmpty(directory)) {
            return false;
        }

        try {
            return client.changeWorkingDirectory(directory);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_CHANGE_DIR_ERROR, e);
        }
    }

    public boolean backParent() {
        return cd("..");
    }

    public String pwd() {
        try {
            return client.printWorkingDirectory();
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_PWD_ERROR, e);
        }
    }

    public boolean exist(String path) {
        String fileName = FileNameUtil.getName(path);
        String dir = StrUtil.removeSuffix(path, fileName);
        List<String> names = ls(dir);
        if (CollectionUtils.isEmpty(names) || StringUtils.isEmpty(fileName)) {
            return false;
        }
        for (String name : names) {
            if (fileName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public List<String> ls(String path) {
        final FTPFile[] ftpFiles = lsFiles(path);

        final List<String> fileNames = new ArrayList<>();
        for (FTPFile ftpFile : ftpFiles) {
            fileNames.add(ftpFile.getName());
        }
        return fileNames;
    }

    public FTPFile[] lsFiles(String path) {
        String pwd = null;
        if (StringUtils.isNotEmpty(path)) {
            pwd = pwd();
            cd(path);
        }

        FTPFile[] ftpFiles;
        try {
            ftpFiles = this.client.listFiles();
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_LS_ERROR, e);
        } finally {
            // 回到原目录
            cd(pwd);
        }

        return ftpFiles;
    }

    public boolean mkdir(String dir) {
        try {
            return this.client.makeDirectory(dir);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_MKDIR_ERROR, e);
        }
    }


    public boolean delFile(String path) {
        String pwd = pwd();
        String fileName = FileNameUtil.getName(path);
        String dir = StrUtil.removeSuffix(path, fileName);
        cd(dir);
        try {
            return client.deleteFile(fileName);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_DEL_ERROR, e);
        } finally {
            // 回到原目录
            cd(pwd);
        }
    }

    public boolean delDir(String dirPath) {
        FTPFile[] dirs = lsFiles(dirPath);
        String name;
        String childPath;
        for (FTPFile ftpFile : dirs) {
            name = ftpFile.getName();
            childPath = String.format("{%s}/{%s}", dirPath, name);
            if (ftpFile.isDirectory()) {
                if (!".".equals(name) && !"..".equals(name)) {
                    delDir(childPath);
                }
            } else {
                delFile(childPath);
            }
        }

        try {
            return this.client.removeDirectory(dirPath);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_DEL_ERROR, dirPath, e);
        }
    }

    public boolean upload(String path, File file) {
        if (file == null) {
            return false;
        }
        return upload(path, file.getName(), file);
    }

    public boolean upload(String path, String fileName, File file) {
        try (InputStream in = FileUtil.getInputStream(file)) {
            return upload(path, fileName, in);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_UPLOAD_ERROR, path + fileName, e);
        }
    }

    public boolean upload(String path, String fileName, InputStream fileStream) {
        try {
            // 需要指定文件传输类型，否则默认是ASCII类型，会导致二进制文件传输损坏
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_BINARY_FILE_TYPE_ERROR, e);
        }

        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }

        if (StringUtils.isNotEmpty(path)) {
            mkdir(path);
            boolean isOk = cd(path);
            if (!isOk) {
                return false;
            }
        }

        try {
            return client.storeFile(fileName, fileStream);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_UPLOAD_ERROR, path + fileName, e);
        } finally {
            if (this.backToPwd) {
                cd(pwd);
            }
        }
    }


    public InputStream download(String path, String fileName) {
        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }
        cd(path);
        try {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            return client.retrieveFileStream(fileName);
        } catch (IOException e) {
            throw new MyException(OssErrorCodeEnum.FTP_DOWNLOAD_ERROR, path + fileName, e);
        } finally {
            if (backToPwd) {
                cd(pwd);
            }
        }
    }

    public void close() {
        ftpClientPool.returnObject(client);
    }

    public void setClient(FTPClient client) {
        this.client = client;
    }
}