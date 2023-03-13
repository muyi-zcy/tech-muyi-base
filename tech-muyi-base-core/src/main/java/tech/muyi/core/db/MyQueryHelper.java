package tech.muyi.core.db;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.DO.MyBaseDO;
import tech.muyi.common.constant.enumtype.ConditionEnum;
import tech.muyi.common.query.MyBaseQuery;
import tech.muyi.common.query.QueryCondition;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.util.*;

/**
 * description: PageQueryHelper
 * date: 2022/1/9 20:06
 * author: muyi
 * version: 1.0
 */
public class MyQueryHelper {
    public static <T extends MyBaseDO> void queryPageConfig(IPage<T> page, MyBaseQuery baseQuery) {
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
    public static <T extends MyBaseDO> LambdaQueryWrapper<T> createQueryWrapper(MyBaseQuery baseQuery) {

        if (baseQuery == null) {
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM);
        }

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(baseQuery.getSort())) {
            switch (baseQuery.getSort()) {
                case "DESC":
                    queryWrapper.orderByDesc("ID");
                    break;
                case "ASC":
                    queryWrapper.orderByAsc("ID");
                    break;
                default:
            }
        }

        // 排序
        if (MapUtils.isNotEmpty(baseQuery.getFieldSort())) {
            Map<String, String> fieldSort = baseQuery.getFieldSort();
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
        if (CollectionUtil.isNotEmpty(baseQuery.getConditions())) {
            List<QueryCondition> conditions = baseQuery.getConditions();
            conditions.forEach(item -> {
                convertConditions(queryWrapper, item);
            });
        }

        if (CollectionUtils.isNotEmpty(baseQuery.getSelectField())) {
            List<String> selecrColumn = baseQuery.getSelectField();
            if (CollectionUtil.isNotEmpty(selecrColumn)) {
                selecrColumn.forEach(queryWrapper::select);
            }
        }
        LambdaQueryWrapper<T> lambdaQueryWrapper = queryWrapper.lambda();


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
        ConditionEnum conditionEnum = ConditionEnum.getConditionByCode(queryCondition.getOperator().toUpperCase());
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
                if (ArrayUtil.isArray(queryCondition.getValue())) {
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
                throw new MyException(CommonErrorCodeEnum.INVALID_PARAM, "请规范查询条件！");
        }
    }
}
