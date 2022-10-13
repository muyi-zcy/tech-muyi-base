package tech.muyi.db;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.constant.enumtype.ConditionEnum;
import tech.muyi.common.query.BaseQuery;
import tech.muyi.common.query.QueryCondition;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.util.List;
import java.util.Map;

/**
 * description: PageQueryHelper
 * date: 2022/1/9 20:06
 * author: muyi
 * version: 1.0
 */
public class PageQueryHelper {
    public static void queryPageConfig(IPage page, BaseQuery baseQuery) {
        baseQuery.setSize(page.getSize());
        baseQuery.setCurrent(page.getCurrent());
        baseQuery.setTotal(page.getTotal());
    }

    public static QueryWrapper createQueryWrapper(BaseQuery baseQuery) {

        if(baseQuery == null){
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM);
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        if(StringUtils.isNotEmpty(baseQuery.getSort())){
            switch (baseQuery.getSort()){
                case "DESC":
                    queryWrapper.orderByDesc("ID");
                    break;
                case "ASC":
                    queryWrapper.orderByAsc("ID");
                    break;
            }
        }

        // 排序
        if(MapUtils.isNotEmpty(baseQuery.getFieldSort())){
            Map<String,String> fieldSort = baseQuery.getFieldSort();
            for (String field : fieldSort.keySet()){
                switch (fieldSort.get(field)){
                    case "DESC":
                        queryWrapper.orderByDesc(field);
                        break;
                    case "ASC":
                        queryWrapper.orderByAsc(field);
                        break;
                }
            }
        }

       // 查询条件
        if(CollectionUtil.isNotEmpty(baseQuery.getConditions())){
            List<QueryCondition> conditions = baseQuery.getConditions();
            conditions.forEach(item->{
                if(StringUtils.isEmpty(item.getCondition())){
                    return;
                }
                ConditionEnum conditionEnum = ConditionEnum.getConditionByCode(item.getCondition().toUpperCase());
                if(conditionEnum == null){
                    return;
                }
                switch (conditionEnum){
                    case AND:
                        break;
                    case OR:
                        break;
                    case IN:
                        queryWrapper.in(item.getColumn(),item.getValue());
                        break;
                    case NOT:
                        queryWrapper.notIn(item.getColumn(),item.getValue());
                        break;
                    case LIKE:
                        queryWrapper.like(item.getColumn(),item.getValue());
                        break;
                    case EQ:
                        queryWrapper.eq(item.getColumn(),item.getValue());
                        break;
                    case NE:
                        queryWrapper.ne(item.getColumn(),item.getValue());
                        break;
                    case GT:
                        queryWrapper.gt(item.getColumn(),item.getValue());
                        break;
                    case GE:
                        queryWrapper.ge(item.getColumn(),item.getValue());
                        break;
                    case LT:
                        queryWrapper.lt(item.getColumn(),item.getValue());
                        break;
                    case LE:
                        queryWrapper.le(item.getColumn(),item.getValue());
                        break;
                    case IS_NULL:
                        queryWrapper.isNull(item.getColumn());
                        break;
                    case IS_NOT_NULL:
                        queryWrapper.isNotNull(item.getColumn());
                        break;
                    case BETWEEN:
                        Object objBegin;
                        Object objEnd;
                        if(ArrayUtil.isArray(item.getValue())){
                            Object[] objects = (Object[]) item.getValue();
                            if(objects.length > 1){
                                objBegin = objects[0];
                                objEnd = objects[objects.length-1];
                            }else {
                                objBegin = objects[0];
                                objEnd = objects[0];
                            }
                        }else {
                            objBegin = item.getValue();
                            objEnd = item.getValue();
                        }
                        queryWrapper.between(item.getColumn(),objBegin,objEnd);
                        break;
                    case ASC:
                        queryWrapper.orderByAsc(item.getValue());
                        break;
                    case DESC:
                        queryWrapper.orderByDesc(item.getValue());
                        break;

                }
            });
        }

        if(CollectionUtils.isNotEmpty(baseQuery.getSelectColumn())) {
            List<String> selecrColumn = baseQuery.getSelectColumn();
            if (CollectionUtil.isNotEmpty(selecrColumn)) {
                selecrColumn.forEach(queryWrapper::select);
            }
        }
        return queryWrapper;
    }
}
