package tech.muyi.common.query;

import lombok.Data;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:20
 */

@Data
public class MyPage {
    private static Long MAX_PAGE_SIZE = 2000L;
    private static Long DEFAULT_PAGE_SIZE = 200L;
    private Long size;
    private Long current;
    private Long total;

    public MyPage() {
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

    public void setCurrent(Long current) {
        this.current = current;
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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getOffset() {
        return (this.getCurrent() - 1) * this.getSize();
    }
}
