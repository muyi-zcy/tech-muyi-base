package tech.muyi.oss;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.muyi.exception.MyException;
import tech.muyi.oss.exception.OssErrorCodeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/5/2
 **/
@Slf4j
@Service
public class OssTemplate {
    @Autowired
    private MinioClient minioClient;

    public void setMinioClient(MinioClient minioClient) {
        this.minioClient = minioClient;
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
            log.error("存储桶【{}】查询失败！异常原因：{}", bucketName, e);
            throw new MyException(OssErrorCodeEnum.EXIST_BUCKET_ERROR, e);
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
            log.error("存储桶【{}】创建失败！异常原因：{}", bucketName, e);
            throw new MyException(OssErrorCodeEnum.MAKE_BUCKET_ERROR, e);
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
            log.error("存储桶列表查询失败！异常原因：{}", e.getMessage());
            throw new MyException(OssErrorCodeEnum.LIST_BUCKET_ERROR, e);
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
                throw new MyException(OssErrorCodeEnum.EXIST_BUCKET);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】删除失败！异常原因：{}", bucketName, e);
            throw new MyException(OssErrorCodeEnum.REMOVE_BUCKET_ERROR, e);
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
                throw new MyException(OssErrorCodeEnum.EXIST_BUCKET);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】文件列表查询失败！异常原因：{}", bucketName, e);
            throw new MyException(OssErrorCodeEnum.LIST_OBJECTS_ERROR, e);
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
                throw new MyException(OssErrorCodeEnum.EXIST_BUCKET);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("存储桶【{}】文件列表查询失败！异常原因：{}", bucketName, e);
            throw new MyException(OssErrorCodeEnum.LIST_OBJECTS_ERROR, e);
        }
    }

    /**
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     */
    public String putObject(String bucketName, String objectName, InputStream stream, Long objectSize) throws IOException {
        try {
            PutObjectOptions putObjectOptions = new PutObjectOptions(objectSize, PutObjectOptions.MIN_MULTIPART_SIZE);
            minioClient.putObject(bucketName, objectName, stream, putObjectOptions);
            log.info("存储桶【{}】上传文件【{}】成功!", bucketName, objectName);
            return minioClient.getObjectUrl(bucketName, objectName);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidBucketNameException | InvalidKeyException | InvalidResponseException |
                 NoSuchAlgorithmException | XmlParserException e) {
            log.error("存储桶【{}】上传对象【{}】失败！异常原因：{}", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.PUT_OBJECTS_ERROR, e);
        } finally {
            stream.close();
        }
    }

    /**
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param stream     输入流
     * @param objectSize 对象大小
     * @param metadata   元数据
     * @return
     */
    public String putObject(String bucketName, String objectName, InputStream stream, Long objectSize, Map<String, String> metadata) throws IOException {
        try {
            PutObjectOptions putObjectOptions = new PutObjectOptions(objectSize, PutObjectOptions.MIN_MULTIPART_SIZE);
            putObjectOptions.setHeaders(metadata);
            minioClient.putObject(bucketName, objectName, stream, putObjectOptions);
            log.info("存储桶【{}】上传文件【{}】成功!", bucketName, objectName);
            return minioClient.getObjectUrl(bucketName, objectName);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidBucketNameException | InvalidKeyException | InvalidResponseException |
                 NoSuchAlgorithmException | XmlParserException e) {
            log.error("存储桶【{}】上传对象【{}】失败！异常原因：{}", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.PUT_OBJECTS_ERROR, e);
        } finally {
            stream.close();
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
            log.error("存储桶【{}】获取【{}】元数据失败!异常原因：{}", bucketName, objectName, e);
            throw new MyException(OssErrorCodeEnum.STAT_OBJECTS_ERROR, e);
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
            log.error("存储桶【{}】下对象【{}】设置过期时间【{}】失败!,异常原因：{}", bucketName, objectName, expires, e);
            throw new MyException(OssErrorCodeEnum.PRESIGNED_GET_OBJECTS_ERROR, e);
        }
    }
}
