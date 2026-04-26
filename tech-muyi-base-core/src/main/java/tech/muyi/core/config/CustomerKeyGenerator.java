package tech.muyi.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.muyi.id.MyIdGenerator;

/**
 * MyBatis-Plus 自定义主键生成器适配器。
 *
 * <p>作用：将内部的 {@link MyIdGenerator}（雪花/分布式 ID）对接到 MyBatis-Plus 的 {@link IKeyGenerator} 机制。</p>
 *
 * <p>实现细节：
 * <ul>
 *   <li>{@link #executeSql(String)} 返回的是“生成主键的 SQL”，由 MP 在插入语句中拼接执行。</li>
 *   <li>这里使用 {@code select <id> from dual} 的形式，兼容需要通过 SQL 返回主键的生成方式。</li>
 * </ul>
 *
 * <p>注意：当前 {@link #dbType()} 返回 {@link DbType#MYSQL}，但 SQL 中使用了 {@code dual}；
 * 在某些数据库方言中 {@code dual} 不是必须或行为不同，若迁移数据库需要同步调整。</p>
 *
 * @author: muyi
 * @date: 2023/1/11
 **/
@Component
public class CustomerKeyGenerator implements IKeyGenerator {
    @Autowired
    private MyIdGenerator myIdGenerator;

    @Override
    public String executeSql(String incrementerName) {
        // incrementerName 为 MP 传入的序列/增量器名称；此实现使用内部 ID 生成器，因此不依赖该参数。
        String id = myIdGenerator.nextIdStr();
        return "select " + id + " from dual";
    }

    @Override
    public DbType dbType() {
        // 声明目标数据库类型，供 MP 选择合适的方言策略。
        return DbType.MYSQL;
    }
}
