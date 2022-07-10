package tech.muyi.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: muyi
 * @date: 2022/7/10
 **/
@ConfigurationProperties(
        prefix = "muyi.api"
)
@Data
public class ApiConfigProperties {
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String desc;
    /**
     * 版本号
     */
    private String version;
    /**
     * 联系
     */
    private Contact contact;

    @Data
    public static class Contact{
        public String name;
        public String url;
        public String email;
    }
}
