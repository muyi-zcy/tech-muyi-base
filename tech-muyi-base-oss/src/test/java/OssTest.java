import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import tech.muyi.oss.OssTemplate;
import tech.muyi.oss.properties.OssProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/5/2
 **/
public class OssTest {
    public static void main(String[] args) throws InvalidPortException, InvalidEndpointException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        OssProperties ossProperties = new OssProperties();
        ossProperties.setEndpoint("124.222.183.53");
        ossProperties.setPort(9090);
        ossProperties.setAccessKey("admin");
        ossProperties.setSecretKey("zcy_nemo@aliyun.com");

        MinioClient minioClient = new MinioClient(
                ossProperties.getEndpoint(),
                ossProperties.getPort(),
                ossProperties.getAccessKey(),
                ossProperties.getSecretKey(),false);

        OssTemplate ossTemplate = new OssTemplate();
        ossTemplate.setMinioClient(minioClient);

        File file = new File("/home/muyi/Pictures/muyi.jpg");
        InputStream inputStream = new FileInputStream(file);
        Map<String,String> map =new HashMap<>();
        map.put("w","dwedwed    ");
        map.put("name","b");
        map.put("c","c");
        System.out.println(ossTemplate.putObject("demo","muyi.jpg",inputStream,file.length(),map));

        Iterable<Result<Item>> myObjects = ossTemplate.listObjects("demo");
        for (Result<Item> result : myObjects) {
            Item item = result.get();
            System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
        }
        System.out.println(ossTemplate.statObject("demo","muyi.jpg"));
        System.out.println(ossTemplate.presignedGetObject("demo","muyi.jpg",61000));
    }
}
