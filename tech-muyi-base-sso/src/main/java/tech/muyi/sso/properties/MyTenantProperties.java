package tech.muyi.sso.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(
        prefix = "muyi.tenant"
)
@Configuration
@Data
public class MyTenantProperties {
    //    是否启动
    private boolean enable = false;

    //    租户字段
    private String tenantIdColumn = "tenantId";

    //      忽略租户的表名
    private List<String> ignoreTable = new ArrayList<>();


    //    超级租户,可以查看所有租户的数据,可以删除所有租户的数据,可以修改所有租户的数据,不可以新增租户数据
    private List<String> superTenantId = new ArrayList<>();

    //    公共租户ID，数据向所有租户共享，如果是null，则无公共租户
    private List<String> commonTenantId = new ArrayList<>();
}
