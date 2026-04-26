package tech.muyi.core.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.DO.MyBaseDO;
import tech.muyi.common.constant.enumtype.ConditionEnum;
import tech.muyi.common.constant.enumtype.EnumCache;
import tech.muyi.core.db.query.MyQuery;
import tech.muyi.core.db.query.QueryCondition;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 查询条件处理器，主要处理前端传入的条件，强调“不额外干涉后端逻辑”
 * date: 2022/1/9 20:06
 * author: muyi
 */
public class MyQueryHelper {
    public static <T extends MyBaseDO> void queryPageConfig(IPage<T> page, MyQuery baseQuery) {
        // 将 MyBatis-Plus 的分页结果回填到 MyQuery 中，便于上层统一拿到 size/current/total 等元数据。
        baseQuery.setSize(page.getSize());
        baseQuery.setCurrent(page.getCurrent());
        baseQuery.setTotal(page.getTotal());
    }

    /**
     * 根据前端传入的参数生成查询包装器
     *
     * @param baseQuery
     * @param <T>
     * @return
     */
    public static <T extends MyBaseDO> LambdaQueryWrapper<T> createQueryWrapper(MyQuery baseQuery) {

        if (baseQuery == null) {
            // 统一错误码：前端/调用方未按协议传查询对象。
            throw new MyException(CommonErrorCodeEnum.QUERY_PARAM_ERROR);
        }

        boolean isDescById = false;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(baseQuery.getSort())) {
            // sort 仅对 ID 做默认排序，避免调用方传入任意字段导致不可控的慢 SQL/索引失效。
            switch (baseQuery.getSort()) {
                case "DESC":
                    queryWrapper.orderByDesc("ID");
                    isDescById = true;
                    break;
                case "ASC":
                    queryWrapper.orderByAsc("ID");
                    break;
                default:
            }
        }

        // 排序
        if (MapUtils.isNotEmpty(baseQuery.getSortField())) {
            // sortField 支持多字段排序，调用方需自行保证字段合法性（避免注入/非法列名）。
            Map<String, String> fieldSort = baseQuery.getSortField();
            for (String field : fieldSort.keySet()) {
                switch (fieldSort.get(field).toUpperCase()) {
                    case "DESC":
                        queryWrapper.orderByDesc(field);
                        break;
                    case "ASC":
                        queryWrapper.orderByAsc(field);
                        break;
                    default:
                }
            }
        }

        // 查询条件
        if (CollectionUtils.isNotEmpty(baseQuery.getConditions())) {
            List<QueryCondition> conditions = baseQuery.getConditions();
            conditions.forEach(item -> {
                // 条件树递归转换：支持 AND/OR 嵌套，但复杂场景仍建议使用后端固定 SQL 或白名单字段策略。
                convertConditions(queryWrapper, item);
            });
        }

        if (CollectionUtils.isNotEmpty(baseQuery.getSelectField())) {
            List<String> selectColumn = baseQuery.getSelectField();
            if (CollectionUtils.isNotEmpty(selectColumn)) {
                // 只查指定列：用于减少大表/宽表的 IO，但同样要求调用方字段是白名单（否则存在注入风险）。
                queryWrapper.select(selectColumn);
            }
        }

        LambdaQueryWrapper<T> lambdaQueryWrapper = queryWrapper.lambda();

        if (baseQuery.getLastId() != null) {
            // lastId：用于“基于 ID 的游标分页/滚动加载”，配合 ID 排序方向决定使用 lt/gt。
            if (isDescById) {
                lambdaQueryWrapper.lt(T::getId, baseQuery.getLastId());
            } else {
                lambdaQueryWrapper.gt(T::getId, baseQuery.getLastId());
            }
        }

        if (CollectionUtils.isNotEmpty(baseQuery.getNotIds())) {
            // 排除指定 ID 集合：常用于“已展示数据不再返回”的去重。
            lambdaQueryWrapper.notIn(T::getId, baseQuery.getNotIds());
        }

        // 将 wrapper 缓存到查询对象中，便于后续 service/dao 层复用或调试查看最终条件。
        baseQuery.setLambdaQueryWrapper(lambdaQueryWrapper);
        return lambdaQueryWrapper;
    }

    /**
     * 编排嵌套查询条件（强烈建议复杂查询使用sql片段，防止出现慢sql、错误sql、sql注入）
     *
     * @param queryWrapper
     * @param queryCondition
     * @param <T>
     */
    private static <T extends MyBaseDO> void convertConditions(QueryWrapper<T> queryWrapper, QueryCondition queryCondition) {
        // operator 统一转为枚举，避免魔法字符串散落；不识别的 operator 直接视为参数错误。
        ConditionEnum conditionEnum = ConditionEnum.findCondition(queryCondition.getOperator().toUpperCase());
        if (conditionEnum == null) {
            throw new MyException(CommonErrorCodeEnum.QUERY_PARAM_ERROR);
        }
        switch (conditionEnum) {
            case AND:
                if (CollectionUtils.isNotEmpty(queryCondition.getChildren())) {
                    queryCondition.getChildren().forEach(item -> {
                        queryWrapper.and(tQueryWrapper -> {
                            convertConditions(tQueryWrapper, item);
                        });
                    });
                }
                break;
            case OR:
                if (CollectionUtils.isNotEmpty(queryCondition.getChildren())) {
                    queryCondition.getChildren().forEach(item -> {
                        queryWrapper.or(tQueryWrapper -> {
                            convertConditions(tQueryWrapper, item);
                        });
                    });
                }
                break;
            case IN:
                // IN 值兼容数组/集合/单值三类，减少前端序列化差异导致的解析失败。
                if (queryCondition.getValue().getClass().isArray()) {
                    queryWrapper.in(queryCondition.getDbColumn(), (Object[]) queryCondition.getValue());
                } else if (queryCondition.getValue() instanceof Collection) {
                    queryWrapper.in(queryCondition.getDbColumn(), ((Collection<?>) queryCondition.getValue()).toArray());
                }else {
                    queryWrapper.in(queryCondition.getDbColumn(), queryCondition.getValue());
                }
                break;
            case NOT:
                // NOT IN 的值同样做数组/集合/单值兼容。
                if (queryCondition.getValue().getClass().isArray()) {
                    queryWrapper.notIn(queryCondition.getDbColumn(), (Object[]) queryCondition.getValue());
                } else if (queryCondition.getValue() instanceof Collection) {
                    queryWrapper.notIn(queryCondition.getDbColumn(), ((Collection<?>) queryCondition.getValue()).toArray());
                }else {
                    queryWrapper.notIn(queryCondition.getDbColumn(), queryCondition.getValue());
                }
                break;
            case LIKE:
                queryWrapper.like(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case NOT_LIKE:
                queryWrapper.notLike(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case EQ:
                queryWrapper.eq(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case NE:
                queryWrapper.ne(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case GT:
                queryWrapper.gt(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case GE:
                queryWrapper.ge(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case LT:
                queryWrapper.lt(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case LE:
                queryWrapper.le(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case IS_NULL:
                queryWrapper.isNull(queryCondition.getDbColumn());
                break;
            case IS_NOT_NULL:
                queryWrapper.isNotNull(queryCondition.getDbColumn());
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                // BETWEEN/NOT_BETWEEN：
                // - value 若为数组则取首尾作为区间端点；否则按同值区间处理（等价于 =）
                Object objBegin;
                Object objEnd;
                if (queryCondition.getValue().getClass().isArray()) {
                    Object[] objects = (Object[]) queryCondition.getValue();
                    if (objects.length > 1) {
                        objBegin = objects[0];
                        objEnd = objects[objects.length - 1];
                    } else {
                        objBegin = objects[0];
                        objEnd = objects[0];
                    }
                } else {
                    objBegin = queryCondition.getValue();
                    objEnd = queryCondition.getValue();
                }
                if (ConditionEnum.BETWEEN.equals(conditionEnum)) {
                    queryWrapper.between(queryCondition.getDbColumn(), objBegin, objEnd);
                } else {
                    queryWrapper.notBetween(queryCondition.getDbColumn(), objBegin, objEnd);
                }
                break;
            case ASC:
                queryWrapper.orderByAsc(Objects.toString(queryCondition.getValue()));
                break;
            case DESC:
                queryWrapper.orderByDesc(Objects.toString(queryCondition.getValue()));
                break;
            default:
                throw new MyException(CommonErrorCodeEnum.QUERY_PARAM_ERROR);
        }
    }
}
