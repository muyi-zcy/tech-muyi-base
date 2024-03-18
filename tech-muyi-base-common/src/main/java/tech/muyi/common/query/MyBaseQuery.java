package tech.muyi.common.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: muyi
 * @date: 2023/7/3
 **/
@Data
public class MyBaseQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Long MAX_PAGE_SIZE = 2000L;

    private static final Long DEFAULT_PAGE_SIZE = 20L;

    // 分页步长
    private Long size;

    // 页码
    private Long current;

    // 总数
    private Long total;

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

    @JsonIgnore
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
