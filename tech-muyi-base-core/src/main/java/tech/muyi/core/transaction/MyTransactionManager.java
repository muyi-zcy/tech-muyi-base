package tech.muyi.core.transaction;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * 手动粗细粒度事务管理器
 *
 * <p>使用场景：
 * <ul>
 *   <li>跨多步写操作、需要显式控制提交/回滚时机的业务流程（而不是依赖 @Transactional 边界）。</li>
 *   <li>在某些框架回调/异步场景中不方便使用声明式事务时，使用模板式事务。</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>{@link #begin()} 返回的 {@link TransactionStatus} 必须配对调用 {@link #commit(TransactionStatus)}
 *       或 {@link #rollback(TransactionStatus)}，否则会造成连接占用与事务悬挂。</li>
 *   <li>隔离级别/传播行为的选择会影响并发一致性与死锁概率，建议由调用方按业务场景显式指定。</li>
 * </ul>
 *
 * @author: muyi
 * @date: 2023/5/30
 **/
@Component
public class MyTransactionManager {

    @Resource
    private DataSourceTransactionManager transactionManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    public TransactionStatus begin() {
        // 默认使用可重复读 + REQUIRED，偏向“读写一致性”与主流 MySQL InnoDB 配置。
        return begin(TransactionDefinition.ISOLATION_REPEATABLE_READ, TransactionDefinition.PROPAGATION_REQUIRED);
    }

    public TransactionStatus begin(Integer isolationLevel, int propagationBehavior) {
        // 通过 TransactionManager 手动开启事务，适用于需要拿到 TransactionStatus 做更细粒度控制的场景。
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(isolationLevel);
        def.setPropagationBehavior(propagationBehavior);
        return transactionManager.getTransaction(def);
    }

    public void commit(TransactionStatus transaction) {
        // 提交事务：调用方需确保仅对“仍然 active”的 TransactionStatus 提交，避免重复提交异常。
        transactionManager.commit(transaction);
    }

    public void rollback(TransactionStatus transaction) {
        // 回滚事务：通常在捕获异常后调用，保证资源释放与状态一致。
        transactionManager.rollback(transaction);
    }

    @Nullable
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        // 模板式事务：Spring 负责 begin/commit/rollback，调用方只需关注 action 内的业务逻辑。
        return transactionTemplate.execute(action);
    }
}
