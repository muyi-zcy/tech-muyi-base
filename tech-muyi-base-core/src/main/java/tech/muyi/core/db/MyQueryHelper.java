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
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM);
        }

        boolean isDescById = false;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(baseQuery.getSort())) {
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
                convertConditions(queryWrapper, item);
            });
        }

        if (CollectionUtils.isNotEmpty(baseQuery.getSelectField())) {
            List<String> selecrColumn = baseQuery.getSelectField();
            if (CollectionUtils.isNotEmpty(selecrColumn)) {
                selecrColumn.forEach(queryWrapper::select);
            }
        }

        LambdaQueryWrapper<T> lambdaQueryWrapper = queryWrapper.lambda();

        if (baseQuery.getLastId() != null) {
            if (isDescById) {
                lambdaQueryWrapper.lt(T::getId, baseQuery.getLastId());
            } else {
                lambdaQueryWrapper.gt(T::getId, baseQuery.getLastId());
            }
        }

        if (CollectionUtils.isNotEmpty(baseQuery.getNotIds())) {
            lambdaQueryWrapper.notIn(T::getId, baseQuery.getNotIds());
        }

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
        ConditionEnum conditionEnum = ConditionEnum.findCondition(queryCondition.getOperator().toUpperCase());
        if (conditionEnum == null) {
            return;
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
                queryWrapper.in(queryCondition.getDbColumn(), queryCondition.getValue());
                break;
            case NOT:
                queryWrapper.notIn(queryCondition.getDbColumn(), queryCondition.getValue());
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
                throw new MyException(CommonErrorCodeEnum.INVALID_PARAM);
        }
    }
}
