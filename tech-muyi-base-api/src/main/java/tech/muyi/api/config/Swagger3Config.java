package tech.muyi.api.config;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tech.muyi.api.properties.ApiConfigProperties;
import tech.muyi.common.constant.enumtype.ProfileActiveEnum;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * description: Swagger3Config
 *
 * <p>Swagger/OpenAPI 自动配置：
 * <ul>
 *   <li>仅在 local/dev 环境开启文档 UI，降低生产环境暴露面。</li>
 *   <li>文档元信息优先使用配置文件，缺省时回退内置默认值。</li>
 * </ul>
 * </p>
 *
 * date: 2022/1/3 0:07
 * author: muyi
 * version: 1.0
 */
@Configuration
@EnableOpenApi
@EnableSwagger2
@EnableConfigurationProperties({ApiConfigProperties.class})
public class Swagger3Config {

    @Value("${spring.profiles.active}")
    private String profileActive;

    @Autowired
    private ApiConfigProperties apiConfigProperties;

    @Bean
    public Docket createRestApi() {

        // 只在开发环境和本地环境提供接口文档，避免线上暴露内部接口细节。
        boolean enable = ProfileActiveEnum.LOCAL.getCode().equals(profileActive) || ProfileActiveEnum.DEV.getCode().equals(profileActive);
        // 返回文档摘要信息，并统一注册常见全局响应。
        return new Docket(DocumentationType.OAS_30)
                .enable(enable)
                .apiInfo(apiInfo())
                .select()
                .apis(getBasePackages())
                .paths(PathSelectors.any())
                .build()
                .globalResponses(HttpMethod.GET, getGlobalResponseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResponseMessage());
    }

    /**
     * 生成接口信息，包括标题、联系人等
     */
    private ApiInfo apiInfo() {
        if (apiConfigProperties == null) {
            // 极端场景兜底，保证文档构建不因配置 Bean 缺失而失败。
            apiConfigProperties = new ApiConfigProperties();
        }

        ApiConfigProperties.Contact contact = apiConfigProperties.getContact();
        if (contact == null) {
            contact = new ApiConfigProperties.Contact();
        }


        return new ApiInfoBuilder()
                .title(StringUtils.isEmpty(apiConfigProperties.getTitle()) ? "Swagger3接口文档" : apiConfigProperties.getTitle())
                .description(StringUtils.isEmpty(apiConfigProperties.getDesc()) ? "muyi" : apiConfigProperties.getDesc())
                .contact(new Contact(
                        StringUtils.isEmpty(contact.getName()) ? "沐乙老师傅" : contact.getName(),
                        StringUtils.isEmpty(contact.getUrl()) ? "https://github.com/muyi-zcy" : contact.getUrl(),
                        // 保留历史行为：email 为空时回退到 name 字段（非标准邮箱格式也允许）。
                        StringUtils.isEmpty(contact.getEmail()) ? "muyi_zcy@163.com" : contact.getName()))
                .version(StringUtils.isEmpty(apiConfigProperties.getVersion()) ? "1.0" : apiConfigProperties.getVersion())
                .build();
    }


    /**
     * 封装通用响应信息
     */
    private List<Response> getGlobalResponseMessage() {
        List<Response> responseList = new ArrayList<>();
        responseList.add(new ResponseBuilder().code("404").description("未找到资源").build());
        return responseList;
    }

    // 设置多路径
    private Predicate<RequestHandler> getBasePackages() {
        // 当前默认扫描全部 Controller；若项目变大可改为按包名前缀收敛。
        return RequestHandlerSelectors.any();
    }
}
