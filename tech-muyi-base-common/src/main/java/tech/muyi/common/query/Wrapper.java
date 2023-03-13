package tech.muyi.common.query;

import tech.muyi.common.DO.MyBaseDO;

/**
 * @author: muyi
 * @date: 2023/3/5
 **/
public interface Wrapper<T extends MyBaseDO> {


    /**
     * 等于 =
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition eq(T column, Object val);


    /**
     * 不等于 &lt;&gt;
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition ne(T column, Object val);


    /**
     * 大于 &gt;
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition gt(T column, Object val);


    /**
     * 大于等于 &gt;=
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition ge(T column, Object val);


    /**
     * 小于 &lt;
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition lt(T column, Object val);


    /**
     * 小于等于 &lt;=
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition le(T column, Object val);

    /**
     * BETWEEN 值1 AND 值2
     *
     * @param column    字段
     * @param val1      值1
     * @param val2      值2
     * @return QueryCondition
     */
    QueryCondition between(T column, Object val1, Object val2);

    /**
     * NOT BETWEEN 值1 AND 值2
     *
     * @param column    字段
     * @param val1      值1
     * @param val2      值2
     * @return QueryCondition
     */
    QueryCondition notBetween(T column, Object val1, Object val2);



    /**
     * LIKE '%值%'
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition like(T column, Object val);



    /**
     * NOT LIKE '%值%'
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition notLike(T column, Object val);



    /**
     * NOT LIKE '%值'
     *
     * @param column
     * @param val
     * @return QueryCondition
     */
    QueryCondition notLikeLeft(T column, Object val);


    /**
     * NOT LIKE '值%'
     *
     * @param column
     * @param val
     * @return QueryCondition
     */
    QueryCondition notLikeRight(T column, Object val);


    /**
     * LIKE '%值'
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition likeLeft(T column, Object val);


    /**
     * LIKE '值%'
     *
     * @param column    字段
     * @param val       值
     * @return QueryCondition
     */
    QueryCondition likeRight(T column, Object val);

}
