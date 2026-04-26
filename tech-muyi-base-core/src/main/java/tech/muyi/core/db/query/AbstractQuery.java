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
 * 查询对象基类（面向 DTO 的查询入参），用于承载：
 * - 条件树（{@link QueryCondition}）
 * - 选择字段、排序字段、分组字段
 * - 以及 MyBatis-Plus 的 {@link LambdaQueryWrapper}（运行期构建，不参与 JSON）
 *
 * <p>设计原则：保持“查询参数协议”与“后端 wrapper 构建”解耦；wrapper 由 helper/服务层构建并回填。</p>
 *
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
        // 允许直接传数据库列名；调用方应保证列名来自白名单，避免拼接式注入风险。
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
        // Lambda 列选择：从 getter 方法名推导字段名（camelCase -> underline_case）。
        selectField.add(parseColumn(column));
        return this;
    }

    public AbstractQuery orderByDesc(SFunction<T, ?> column) {
        if(sortField == null){
            sortField = new LinkedHashMap<>();
        }
        // LinkedHashMap 保持添加顺序，从而让多字段排序的优先级可预测。
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
        // groupBy 也需要调用方保证列名合法；该层不做额外校验以保持通用性。
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
        // notIds 常用于“排除已知记录”，比如滚动加载/去重。
        notIds.add(id);
        return this;
    }

    private String parseColumn(SFunction<T, ?> column){
        // MyBatis-Plus 的 Lambda 提取：从序列化的 lambda 中得到实现方法名，再转字段名。
        // 约束：要求 JavaBean 命名规范（getXxx/isXxx），否则 methodToProperty 可能解析失败。
        LambdaMeta meta = LambdaUtils.extract(column);
        String camelCaseString =  PropertyNamer.methodToProperty(meta.getImplMethodName());
        String underscoreString = camelCaseString.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        return underscoreString;
    }

}
