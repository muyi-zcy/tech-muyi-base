package tech.muyi.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API 文档配置属性。
 *
 * <p>绑定前缀：`muyi.api`，用于驱动 Swagger 文档展示信息。
 * 所有字段均可为空，运行时由配置类提供默认值兜底。</p>
 *
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
        // 联系人姓名（可选）
        public String name;
        // 联系主页（可选）
        public String url;
        // 联系邮箱（可选）
        public String email;
    }
}
