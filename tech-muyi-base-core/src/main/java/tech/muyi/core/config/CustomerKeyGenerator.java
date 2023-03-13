package tech.muyi.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.muyi.id.MyIdGenerator;

/**
 * @author: muyi
 * @date: 2023/1/11
 **/
@Component
public class CustomerKeyGenerator implements IKeyGenerator {
    @Autowired
    private MyIdGenerator myIdGenerator;

    @Override
    public String executeSql(String incrementerName) {
        String id = myIdGenerator.nextIdStr();
        return "select " + id + " from dual";
    }

    @Override
    public DbType dbType() {
        return DbType.MYSQL;
    }
}
