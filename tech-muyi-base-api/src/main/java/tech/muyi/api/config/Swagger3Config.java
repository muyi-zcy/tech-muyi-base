package tech.muyi.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import tech.muyi.common.constant.enumtype.ProfileActiveEnum;


import java.util.ArrayList;
import java.util.List;

/**
 * description: Swagger3Config
 * date: 2022/1/3 0:07
 * author: muyi
 * version: 1.0
 */
@Configuration
@EnableOpenApi
public class Swagger3Config {
    @Value("${spring.profiles.active}")
    private String profileActive;

    private String basePackage;
    @Bean
    public Docket createRestApi() {
        boolean enable = ProfileActiveEnum.LOCAL.getCode().equals(profileActive) || ProfileActiveEnum.DEV.getCode().equals(profileActive);
        //返回文档摘要信息
        return new Docket(DocumentationType.OAS_30)
                .enable(enable)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .globalResponses(HttpMethod.GET, getGlobalResponseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResponseMessage());
    }

    /**
     * 生成接口信息，包括标题、联系人等
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3接口文档")
                .description("muyi")
                .contact(new Contact("muyi","www.muyi.top","zcy_nemo@aliyun.com"))
                .version("1.0")
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
}
