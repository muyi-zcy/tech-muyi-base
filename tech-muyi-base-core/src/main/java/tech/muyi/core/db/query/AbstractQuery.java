package tech.muyi.core.db.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.ibatis.reflection.property.PropertyNamer;
import tech.muyi.common.DTO.MyBaseDTO;
import tech.muyi.common.constant.enumtype.SortEnum;
import tech.muyi.common.query.MyBaseQuery;

import java.util.*;

/**
 * @author: muyi
 * @date: 2023/3/5
 **/
@Data
public class AbstractQuery<T extends MyBaseDTO> extends MyBaseQuery implements Wrapper<T> {

    @JsonIgnore
    public transient LambdaQueryWrapper<T> lambdaQueryWrapper;

    // 查询条件
    private List<QueryCondition> conditions;

    // 查询字段
    private List<String> selectField;

    // 排序字段
    private Map<String, String> sortField;

    // 分组
    protected Set<String> groupBy;

    // 排除ID
    private List<Long> notIds;


    public AbstractQuery select(String column) {
        if (selectField == null) {
            selectField = new LinkedList<>();
        }
        selectField.add(column);
        return this;
    }

    public final AbstractQuery select(SFunction<T, ?>... columns) {
        for (SFunction<T, ?> sFunction : columns) {
            select(sFunction);
        }
        return this;
    }


    public AbstractQuery select(SFunction<T, ?> column) {
        if (selectField == null) {
            selectField = new LinkedList<>();
        }
        selectField.add(parseColumn(column));
        return this;
    }

    public AbstractQuery orderByDesc(SFunction<T, ?> column) {
        if(sortField == null){
            sortField = new LinkedHashMap<>();
        }
        sortField.put(parseColumn(column), SortEnum.DESC.name());
        return this;
    }

    public AbstractQuery orderByAsc(SFunction<T, ?> column) {
        if(sortField == null){
            sortField = new LinkedHashMap<>();
        }
        sortField.put(parseColumn(column), SortEnum.ASC.name());
        return this;
    }

    public AbstractQuery orderBy(SFunction<T, ?> column, SortEnum sortEnum) {
        if(sortField == null){
            sortField = new LinkedHashMap<>();
        }
        sortField.put(parseColumn(column), sortEnum.name());
        return this;
    }

    public AbstractQuery<T> groupBy(String column){
        if(groupBy == null){
            groupBy = new LinkedHashSet<>();
        }
        groupBy.add(column);
        return this;
    }

    public AbstractQuery<T> groupBy(SFunction<T, ?> column){
        return groupBy(parseColumn(column));
    }

    public AbstractQuery<T> notId(Long id){
        if(notIds == null){
            notIds = new ArrayList<>();
        }
        notIds.add(id);
        return this;
    }

    private String parseColumn(SFunction<T, ?> column){
        LambdaMeta meta = LambdaUtils.extract(column);
        return PropertyNamer.methodToProperty(meta.getImplMethodName());
    }


}
