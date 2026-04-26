package tech.muyi.sso.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.collections4.CollectionUtils;
import tech.muyi.sso.MySsoManager;
import tech.muyi.sso.properties.MyTenantProperties;

@Slf4j
/**
 * 多租户字段处理器。
 *
 * <p>根据当前 SSO 上下文提供 tenantId 条件，并决定是否忽略租户拦截。</p>
 */
public class MyTenantLineHandler implements TenantLineHandler {

    private MySsoManager mySsoManager;

    private MyTenantProperties myTenantProperties;

    public MyTenantLineHandler(MySsoManager mySsoManager, MyTenantProperties myTenantProperties) {
        this.mySsoManager = mySsoManager;
        this.myTenantProperties = myTenantProperties;
    }

    @Override
    public Expression getTenantId() {
        if (mySsoManager.getSsoInfo() == null) {
            // 无登录上下文时返回占位值，防止生成无约束 SQL。
            return new StringValue(getTenantIdColumn());
        }
        return new StringValue(mySsoManager.getSsoInfo().getTenantId());
    }

    /**
     * 多租户插件忽略的表
     *
     * @param tableName 表名
     * @return true 忽略 ; false 不忽略
     */
    @Override
    public boolean ignoreTable(String tableName) {
        if (myTenantProperties == null || !myTenantProperties.isEnable()) {
            return true;
        }
        if (mySsoManager.getSsoInfo() == null) {
            return true;
        }
        if (mySsoManager.isSuperTenant()) {
            return true;
        }

        if (CollectionUtils.isEmpty(myTenantProperties.getIgnoreTable())) {
            return false;
        }

        return myTenantProperties.getIgnoreTable().contains(tableName);
    }
}
