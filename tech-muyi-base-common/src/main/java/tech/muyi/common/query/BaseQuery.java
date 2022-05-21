package tech.muyi.common.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:19
 */
@Data
public class BaseQuery extends MyPage implements Serializable {
    private String sort;
    private Long lastId;
    private Boolean isSearchCount;
    private List<QueryCondition> conditions;
    private List<String> selectColumn;
    private Map<String,String> fieldSort;

    public String getSort() {
        if (StringUtils.isEmpty(this.sort)) {
            return this.sort;
        } else {
            this.sort = StringUtils.upperCase(this.sort);
            return this.sort;
        }
    }

}
