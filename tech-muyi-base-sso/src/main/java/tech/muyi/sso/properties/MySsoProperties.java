package tech.muyi.sso.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import tech.muyi.sso.dto.MySsoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: muyi
 * @date: 2023/9/22
 **/

@ConfigurationProperties(
        prefix = "muyi.sso"
)
@Component
@Data
public class MySsoProperties {

    private boolean enable = false;

    private String tokenKey = "my-sso:";

    private String tag = "token";

    private Long effectiveTime = 60 * 60L;

    // 不需要进行拦截的接口
    private List<String> exclude = new ArrayList<>();

    private Class<? extends MySsoInfo> ssoInfoClass = MySsoInfo.class;

}
