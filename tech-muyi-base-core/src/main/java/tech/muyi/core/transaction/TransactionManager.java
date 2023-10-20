package tech.muyi.core.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
 * @author: muyi
 * @date: 2023/5/30
 **/
@Component
public class TransactionManager {

    @Resource
    private DataSourceTransactionManager transactionManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    public TransactionStatus begin() {
        return begin(TransactionDefinition.ISOLATION_REPEATABLE_READ, TransactionDefinition.PROPAGATION_REQUIRED);
    }

    public TransactionStatus begin(Integer isolationLevel, int propagationBehavior) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(isolationLevel);
        def.setPropagationBehavior(propagationBehavior);
        return transactionManager.getTransaction(def);
    }

    public void commit(TransactionStatus transaction) {
        transactionManager.commit(transaction);
    }

    public void rollback(TransactionStatus transaction) {
        transactionManager.rollback(transaction);
    }

    @Nullable
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return transactionTemplate.execute(action);
    }
}
