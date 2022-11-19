package tech.muyi.db;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tech.muyi.common.DO.MyBaseDO;
import tech.muyi.common.constant.enumtype.ConditionEnum;
import tech.muyi.common.query.BaseQuery;
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
public class PageQueryHelper {
    public static <T extends MyBaseDO> void queryPageConfig(IPage<T> page, BaseQuery baseQuery) {
        baseQuery.setSize(page.getSize());
        baseQuery.setCurrent(page.getCurrent());
        baseQuery.setTotal(page.getTotal());
    }

    public static <T extends MyBaseDO> LambdaQueryWrapper<T> createQueryWrapper(BaseQuery baseQuery) {

        if (baseQuery == null) {
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM);
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(baseQuery.getSort())) {
            switch (baseQuery.getSort().toUpperCase()) {
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

        if (CollectionUtils.isNotEmpty(baseQuery.getSelectColumn())) {
            List<String> selecrColumn = baseQuery.getSelectColumn();
            if (CollectionUtil.isNotEmpty(selecrColumn)) {
                selecrColumn.forEach(queryWrapper::select);
            }
        }
        return queryWrapper.lambda();
    }

    private static <T extends MyBaseDO> void convertConditions(QueryWrapper<T> queryWrapper, QueryCondition queryCondition) {
        ConditionEnum conditionEnum = ConditionEnum.getConditionByCode(queryCondition.getCondition().toUpperCase());
        if (conditionEnum == null) {
            return;
        }
        switch (conditionEnum) {
            case AND:
                if(CollectionUtils.isNotEmpty(queryCondition.getChildren())) {
                    queryCondition.getChildren().forEach(item->{
                        queryWrapper.and(tQueryWrapper -> {
                            convertConditions(tQueryWrapper, item);
                            return tQueryWrapper;
                        });
                    });
                }
                break;
            case OR:
                if(CollectionUtils.isNotEmpty(queryCondition.getChildren())) {
                    queryCondition.getChildren().forEach(item->{
                        queryWrapper.or(tQueryWrapper -> {
                            convertConditions(tQueryWrapper, item);
                            return tQueryWrapper;
                        });
                    });
                }
                break;
            case IN:
                queryWrapper.in(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case NOT:
                queryWrapper.notIn(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case LIKE:
                queryWrapper.like(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case NOT_LIKE:
                queryWrapper.notLike(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case EQ:
                queryWrapper.eq(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case NE:
                queryWrapper.ne(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case GT:
                queryWrapper.gt(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case GE:
                queryWrapper.ge(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case LT:
                queryWrapper.lt(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case LE:
                queryWrapper.le(queryCondition.getColumn(), queryCondition.getValue());
                break;
            case IS_NULL:
                queryWrapper.isNull(queryCondition.getColumn());
                break;
            case IS_NOT_NULL:
                queryWrapper.isNotNull(queryCondition.getColumn());
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
                    queryWrapper.between(queryCondition.getColumn(), objBegin, objEnd);
                } else {
                    queryWrapper.notBetween(queryCondition.getColumn(), objBegin, objEnd);
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
