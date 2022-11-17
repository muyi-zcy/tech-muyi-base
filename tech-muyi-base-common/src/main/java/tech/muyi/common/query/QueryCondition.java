package tech.muyi.common.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description: QueryCondition
 * date: 2022/1/9 17:54
 * author: muyi
 * version: 1.0
 */
@Data
public class QueryCondition{
    private String column;
    private String condition;
    private Object value;
    private List<QueryCondition> children;
}
