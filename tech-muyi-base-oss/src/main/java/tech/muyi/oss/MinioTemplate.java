package tech.muyi.oss;

import cn.hutool.core.io.FileUtil;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.exception.MyException;
import tech.muyi.oss.exception.OssErrorCodeEnum;
import tech.muyi.oss.properties.MinioProperties;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: muyi
 * @date: 2022/5/2
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties({MinioProperties.class})
@ConditionalOnClass({MinioClient.class})
@ConditionalOnProperty(name = {"muyi.file.minio.enable"}, havingValue = "true")
public class MinioTemplate {

    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        try {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectionPool(new ConnectionPool(minioProperties.getMaxActive(), minioProperties.getKeepAliveDuration(), TimeUnit.MINUTES))
                    .connectTimeout(minioProperties.getConnectTimeout(), TimeUnit.SECONDS)
                    .build();
            okHttpClient.connectionPool();
            minioClient = new MinioClient(
                    minioProperties.getEndpoint(),
                    minioProperties.getPort(),
                    minioProperties.getAccessKey(),
                    minioProperties.getSecretKey(),
                    null,
                    false,
                    okHttpClient);

        } catch (InvalidEndpointException | InvalidPortException e) {
            log.error("OSS连接服务器失败，错误原因:", e);
            throw new MyException(OssErrorCodeEnum.OSS_CONN_ERROR, e);
        }
    }

    /**
     * 存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 结果
     */
    public boolean existBucket(String bucketName) {
        try {
            return minioClient.bucketExists(bucketName);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("存储桶【{}】查询失败！异常原因:", bucketName, e);
            throw new MyException(OssErrorCodeEnum.OSS_EXIST_BUCKET_ERROR, e);
        }
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return 结果
     */
    public boolean makeBucket(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                log.info("存储桶【{}】已存在!", bucketName);
            } else {
                minioClient.makeBucket(bucketName);
                log.info("存储桶【{}】创建成功!", bucketName);
            }
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("存储桶【{}】创建失败！异常原因:", bucketName, e);
            throw new MyException(OssErrorCodeEnum.OSS_MAKE_BUCKET_ERROR, e);
        }
        return true;
    }

    /**
     * 查询所有存储桶
     *
     * @return 结果
     */
    public List<Bucket> listBuckets() {
        try {
            // 列出所有存储桶
            return minioClient.listBuckets();
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶列表查询失败！异常原因:", e);
            throw new MyException(OssErrorCodeEnum.OSS_LIST_BUCKET_ERROR, e);
        }
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return 结果
     */
    public boolean removeBucket(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                minioClient.removeBucket(bucketName);
                return true;
            } else {
                log.info("存储桶【{}】不存在!", bucketName);
                throw new MyException(OssErrorCodeEnum.OSS_EXIST_BUCKET);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】删除失败！异常原因:", bucketName, e);
            throw new MyException(OssErrorCodeEnum.OSS_REMOVE_BUCKET_ERROR, e);
        }
    }


    /**
     * 查询存储桶下文件列表
     *
     * @param bucketName 存储桶名称
     * @return 结果
     */
    public Iterable<Result<Item>> listObjects(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                return minioClient.listObjects(bucketName);
            } else {
                log.info("存储桶【{}】不存在!", bucketName);
                throw new MyException(OssErrorCodeEnum.OSS_EXIST_BUCKET);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】文件列表查询失败！异常原因:", bucketName, e);
            throw new MyException(OssErrorCodeEnum.OSS_LIST_OBJECTS_ERROR, e);
        }
    }

    /**
     * @param bucketName 存储桶名称
     * @param prefix     对象名称的前缀
     * @param recursive  是否递归查找，如果是false,就模拟文件夹结构查找
     * @return 结果
     */
    public Iterable<Result<Item>> listObjects(String bucketName, String prefix, boolean recursive) {
        try {
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                return minioClient.listObjects(bucketName, prefix, recursive);
            } else {
                log.info("存储桶【{}】不存在!", bucketName);
                throw new MyException(OssErrorCodeEnum.OSS_EXIST_BUCKET);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】文件列表查询失败！异常原因:", bucketName, e);
            throw new MyException(OssErrorCodeEnum.OSS_LIST_OBJECTS_ERROR, e);
        }
    }

    /**
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     */
    public String uploadObject(String bucketName, String objectName, InputStream stream, String... path) {
        return this.uploadObject(bucketName, objectName, stream, null, path);
    }


    /**
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param file       要上传的文件
     */
    public String uploadObject(String bucketName, String objectName, File file, String... path) {
        InputStream inputStream;
        try {
            if (!objectName.contains(".")) {
                objectName = objectName + "." + FileUtil.getSuffix(file.getName());
            }
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("存储桶【{}】上传对象【{}】失败！异常原因:", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.OSS_PUT_OBJECTS_ERROR, e);
        }
        return this.uploadObject(bucketName, objectName, inputStream, null, path);
    }


    /**
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param file       要上传的文件
     */
    public String uploadObject(String bucketName, String objectName, MultipartFile file, String... path) {
        try {
            if (!objectName.contains(".")) {
                objectName = objectName + "." + FileUtil.getSuffix(file.getOriginalFilename());
            }
            return this.uploadObject(bucketName, objectName, file.getInputStream(), null, path);
        } catch (IOException e) {
            log.error("存储桶【{}】上传对象【{}】失败！异常原因:", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.OSS_PUT_OBJECTS_ERROR, e);
        }
    }


    /**
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param stream     输入流
     * @param metadata   元数据
     * @return
     */
    public String uploadObject(String bucketName, String objectName, InputStream stream, Map<String, String> metadata, String... path) {
        try {
            if (path != null && path.length > 0) {
                objectName = String.join("/", path) + "/" + objectName;
            }
            PutObjectOptions putObjectOptions = new PutObjectOptions(stream.available(), PutObjectOptions.MIN_MULTIPART_SIZE);
            if (metadata != null) {
                putObjectOptions.setHeaders(metadata);
            }
            minioClient.putObject(bucketName, objectName, stream, putObjectOptions);
            log.info("存储桶【{}】上传文件【{}】成功!", bucketName, objectName);
            return minioClient.getObjectUrl(bucketName, objectName);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidBucketNameException | InvalidKeyException | InvalidResponseException |
                 NoSuchAlgorithmException | XmlParserException e) {
            log.error("存储桶【{}】上传对象【{}】失败！异常原因:", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.OSS_PUT_OBJECTS_ERROR, e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("存储桶【{}】上传对象【{}】失败！异常原因:", bucketName, objectName, e);
                throw new MyException(OssErrorCodeEnum.OSS_PUT_OBJECTS_ERROR, e);
            }
        }
    }

    /**
     * 获得对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return 结果
     */
    public ObjectStat statObject(String bucketName, String objectName) {
        try {
            return minioClient.statObject(bucketName, objectName);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】获取【{}】元数据失败!异常原因:", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.OSS_STAT_OBJECTS_ERROR, e);
        }
    }

    /**
     * 生成一个给HTTP GET请求用的presigned URL。浏览器/移动端的客户端可以用这个URL进行下载，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return 结果
     */
    public String presignedGetObject(String bucketName, String objectName, Integer expires) {
        try {
            return minioClient.presignedGetObject(bucketName, objectName, expires);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("存储桶【{}】下对象【{}】设置过期时间【{}】失败!,异常原因:", bucketName, objectName, expires, e);
            throw new MyException(OssErrorCodeEnum.OSS_PRESIGNED_GET_OBJECTS_ERROR, e);
        }
    }


    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName
     */
    public void deleteObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(bucketName, objectName);
        } catch (ErrorResponseException | InsufficientDataException | InvalidBucketNameException |
                 InvalidResponseException | IOException | InternalException | InvalidKeyException |
                 NoSuchAlgorithmException | XmlParserException e) {
            log.error("存储桶【{}】下对象【{}】删除失败!,异常原因:", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.OSS_OBJECT_DELETE_ERROR, e);
        }
    }

    /**
     * 下载文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return 流
     */
    public InputStream downloadObject(String bucketName, String objectName) {
        try {
            return minioClient.getObject(bucketName, objectName);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】下对象【{}】下载失败!,异常原因:", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.OSS_OBJECT_DOWNLOAD_ERROR, e);
        }
    }
}
