package tech.muyi.common.query;

import tech.muyi.common.DO.MyBaseDO;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author: muyi
 * @date: 2023/3/5
 **/
public interface Func<T extends MyBaseDO> {
    /**


    /**
     * 字段 IS NULL
     * <p>例: isNull("name")</p>
     *
     * @param column    字段
     * @return QueryCondition
     */
    QueryCondition isNull(T column);


    /**
     * 字段 IS NOT NULL
     * <p>例: isNotNull("name")</p>
     *
     * @param column    字段
     * @return QueryCondition
     */
    QueryCondition isNotNull(T column);



    /**
     * 字段 IN (value.get(0), value.get(1), ...)
     * <p>例: in("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * <li> 注意！集合为空若存在逻辑错误，请在 condition 条件中判断 </li>
     * <li> 如果集合为 empty 则不会进行 sql 拼接 </li>
     *
     * @param column    字段
     * @param coll      数据集合
     * @return QueryCondition
     */
    QueryCondition in(T column, Collection<?> coll);


    /**
     * 字段 IN (v0, v1, ...)
     * <p>例: in("id", 1, 2, 3, 4, 5)</p>
     *
     * <li> 注意！数组为空若存在逻辑错误，请在 condition 条件中判断 </li>
     * <li> 如果动态数组为 empty 则不会进行 sql 拼接 </li>
     *
     * @param column    字段
     * @param values    数据数组
     * @return QueryCondition
     */
    QueryCondition in(T column, Object... values);



    /**
     * 字段 NOT IN (value.get(0), value.get(1), ...)
     * <p>例: notIn("id", Arrays.asList(1, 2, 3, 4, 5))</p>
     *
     * @param column    字段
     * @param coll      数据集合
     * @return QueryCondition
     */
    QueryCondition notIn(T column, Collection<?> coll);


    /**
     * 字段 NOT IN (v0, v1, ...)
     * <p>例: notIn("id", 1, 2, 3, 4, 5)</p>
     *
     * @param column    字段
     * @param values    数据数组
     * @return QueryCondition
     */
    QueryCondition notIn(T column, Object... values);



    /**
     * 字段 IN ( sql语句 )
     * <p>!! sql 注入方式的 in 方法 !!</p>
     * <p>例1: inSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例2: inSql("id", "select id from table where id &lt; 3")</p>
     *
     * @param column    字段
     * @param inValue   sql语句
     * @return QueryCondition
     */
    QueryCondition inSql(T column, String inValue);

    /**
     * 字段 &gt; ( sql语句 )
     * <p>例1: gtSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例1: gtSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column
     * @param inValue
     * @return
     */
    QueryCondition gtSql(T column, String inValue);


    /**
     * 字段 >= ( sql语句 )
     * <p>例1: geSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例1: geSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column
     * @param inValue
     * @return
     */
    QueryCondition geSql(T column, String inValue);

   

    /**
     * 字段 &lt; ( sql语句 )
     * <p>例1: ltSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例1: ltSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column
     * @param inValue
     * @return
     */
    QueryCondition ltSql(T column, String inValue);


    /**
     * 字段 <= ( sql语句 )
     * <p>例1: leSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例1: leSql("id", "select id from table where name = 'JunJun'")</p>
     *
     * @param column
     * @param inValue
     * @return
     */
    QueryCondition leSql(T column, String inValue);

    
    /**
     * 字段 NOT IN ( sql语句 )
     * <p>!! sql 注入方式的 not in 方法 !!</p>
     * <p>例1: notInSql("id", "1, 2, 3, 4, 5, 6")</p>
     * <p>例2: notInSql("id", "select id from table where id &lt; 3")</p>
     *
     * @param column    字段
     * @param inValue   sql语句 ---&gt; 1,2,3,4,5,6 或者 select id from table where id &lt; 3
     * @return QueryCondition
     */
    QueryCondition notInSql(T column, String inValue);

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy("id")</p>
     *
     * @param column    单个字段
     * @return QueryCondition
     */
    QueryCondition groupBy(T column);

    /**
     * 分组：GROUP BY 字段, ...
     * <p>例: groupBy(Arrays.asList("id", "name"))</p>
     *
     * @param columns   字段数组
     * @return QueryCondition
     */
    QueryCondition groupBy(List<T> columns);


    /**
     * 分组：GROUP BY 字段, ...
     */
    QueryCondition groupBy(T column, T... columns);

    /**
     * 排序：ORDER BY 字段, ... ASC
     * <p>例: orderByAsc(true, "id")</p>
     *
     * @param column    单个字段
     * @return QueryCondition
     */
    default QueryCondition orderByAsc(T column) {
        return orderBy( true, column);
    }


    /**
     * 排序：ORDER BY 字段, ... ASC
     *
     * @param columns   字段数组
     * @return QueryCondition
     */
    default QueryCondition orderByAsc(List<T> columns) {
        return orderBy(true, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     */
    default QueryCondition orderByAsc(T column, T... columns) {
        return orderBy(true, column, columns);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     *
     * @param column    字段
     * @return QueryCondition
     */
    default QueryCondition orderByDesc(T column) {
        return orderByDesc(column);
    }

    /**
     * 排序：ORDER BY 字段, ... DESC
     *
     * @param columns   字段列表
     * @return QueryCondition
     */
    default QueryCondition orderByDesc(List<T> columns) {
        return orderBy(false, columns);
    }


    default QueryCondition orderByDesc(T column, T... columns) {
        return orderByDesc(column, columns);
    }


    /**
     * 排序：ORDER BY 字段, ...
     * <p>例: orderBy(true, "id")</p>
     *
     * @param isAsc     是否是 ASC 排序
     * @param column    单个字段
     * @return QueryCondition
     */
    QueryCondition orderBy(boolean isAsc, T column);

    /**
     * 排序：ORDER BY 字段, ...
     * <p>例: orderBy(true, Arrays.asList("id", "name"))</p>
     *
     * @param isAsc     是否是 ASC 排序
     * @param columns   字段列表
     * @return QueryCondition
     */
    QueryCondition orderBy(boolean isAsc, List<T> columns);

    /**
     * 排序：ORDER BY 字段, ...
     */
    QueryCondition orderBy(boolean isAsc, T column, T... columns);


    /**
     * HAVING ( sql语句 )
     * <p>例1: having("sum(age) &gt; 10")</p>
     * <p>例2: having("sum(age) &gt; {0}", 10)</p>
     *
     * @param sqlHaving sql 语句
     * @param params    参数数组
     * @return QueryCondition
     */
    QueryCondition having(String sqlHaving, Object... params);


    /**
     * 消费函数
     *
     * @param consumer 消费函数
     * @return QueryCondition
     * @since 3.3.1
     */
    QueryCondition func(Consumer<QueryCondition> consumer);
}
