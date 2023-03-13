package tech.muyi.common.query;

import tech.muyi.common.DO.MyBaseDO;
import tech.muyi.common.constant.enumtype.SortEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author: muyi
 * @date: 2023/3/5
 **/
public class AbstractQuery<T extends MyBaseDO> implements Wrapper<T>,Func<T> {
    protected Map<Function<T, ?>, SortEnum> orderBy;

    protected Set<Function<T, ?>> groupBy;

    protected Set<Function<T, ?>> select;

    protected String last;
    protected String sqlFirst;


    @Override
    public QueryCondition eq(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition ne(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition gt(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition ge(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition lt(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition le(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition between(T column, Object val1, Object val2) {
        return null;
    }

    @Override
    public QueryCondition notBetween(T column, Object val1, Object val2) {
        return null;
    }

    @Override
    public QueryCondition like(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition notLike(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition notLikeLeft(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition notLikeRight(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition likeLeft(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition likeRight(T column, Object val) {
        return null;
    }

    @Override
    public QueryCondition isNull(T column) {
        return null;
    }

    @Override
    public QueryCondition isNotNull(T column) {
        return null;
    }

    @Override
    public QueryCondition in(T column, Collection<?> coll) {
        return null;
    }

    @Override
    public QueryCondition in(T column, Object... values) {
        return null;
    }

    @Override
    public QueryCondition notIn(T column, Collection<?> coll) {
        return null;
    }

    @Override
    public QueryCondition notIn(T column, Object... values) {
        return null;
    }

    @Override
    public QueryCondition inSql(T column, String inValue) {
        return null;
    }

    @Override
    public QueryCondition gtSql(T column, String inValue) {
        return null;
    }

    @Override
    public QueryCondition geSql(T column, String inValue) {
        return null;
    }

    @Override
    public QueryCondition ltSql(T column, String inValue) {
        return null;
    }

    @Override
    public QueryCondition leSql(T column, String inValue) {
        return null;
    }

    @Override
    public QueryCondition notInSql(T column, String inValue) {
        return null;
    }

    @Override
    public QueryCondition groupBy(T column) {
        return null;
    }

    @Override
    public QueryCondition groupBy(List<T> columns) {
        return null;
    }

    @Override
    public QueryCondition groupBy(T column, T... columns) {
        return null;
    }

    @Override
    public QueryCondition orderBy(boolean isAsc, T column) {
        return null;
    }

    @Override
    public QueryCondition orderBy(boolean isAsc, List<T> columns) {
        return null;
    }

    @Override
    public QueryCondition orderBy(boolean isAsc, T column, T... columns) {
        return null;
    }

    @Override
    public QueryCondition having(String sqlHaving, Object... params) {
        return null;
    }

    @Override
    public QueryCondition func(Consumer<QueryCondition> consumer) {
        return null;
    }
}
