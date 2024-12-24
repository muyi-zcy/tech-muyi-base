package tech.muyi.sso.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.collections4.CollectionUtils;
import tech.muyi.sso.MySsoManager;
import tech.muyi.sso.properties.MyTenantProperties;

@Slf4j
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
