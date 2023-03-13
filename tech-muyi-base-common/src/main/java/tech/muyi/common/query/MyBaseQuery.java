package tech.muyi.common.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.DO.MyBaseDO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用于接收前端列表查询及后端对查询的补充
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:19
 */
@Data
public class MyBaseQuery<T extends MyBaseDO> extends AbstractQuery<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Long MAX_PAGE_SIZE = 2000L;

    private static final Long DEFAULT_PAGE_SIZE = 200L;
    private Long size;
    private Long current;
    private Long total;

    private String sort;

    private Long lastId;

    private Boolean isSearchCount;

    private List<QueryCondition> conditions;

    private List<String> selectField;

    private Map<String, String> fieldSort;


    public String getSort() {
        if (StringUtils.isEmpty(this.sort)) {
            return this.sort;
        } else {
            this.sort = StringUtils.upperCase(this.sort);
            return this.sort;
        }
    }

    public Long getSize() {
        return this.size != null && this.size >= 1 ? this.size : DEFAULT_PAGE_SIZE;
    }

    public void setSize(Long size) {
        if (size == null) {
            this.size = DEFAULT_PAGE_SIZE;
        } else if (size > MAX_PAGE_SIZE) {
            this.size = MAX_PAGE_SIZE;
        } else {
            this.size = size;
        }
    }

    public Long getCurrent() {
        return this.current != null && this.current >= 1 ? this.current : 1;
    }


    public Long getPageTotal() {
        if (this.getSize() == null || this.total == null) {
            return 0L;
        } else {
            Long page = this.total / this.getSize();
            if (this.total % this.getSize() > 0) {
                ++page;
            }

            return page;
        }
    }

    public Long getOffset() {
        return (this.getCurrent() - 1) * this.getSize();
    }
}
