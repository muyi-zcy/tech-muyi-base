package tech.muyi.common.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基础参数。
 *
 * <p>约定：
 * <ul>
 *   <li>size/current 允许不传，使用默认值；同时对 size 做上限保护，避免一次拉取过大导致慢查询/内存压力。</li>
 *   <li>total 通常由后端查询回填，用于前端分页展示。</li>
 *   <li>{@link #getPageTotal()} 仅做展示用途，因此标记 {@link JsonIgnore} 避免与 total/size/current 产生协议歧义。</li>
 * </ul>
 *
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
        // size 未传或非法时回退默认值；由 setSize 做上限裁剪。
        return this.size != null && this.size >= 1 ? this.size : DEFAULT_PAGE_SIZE;
    }

    public void setSize(Long size) {
        // 限制单页最大条数，防止调用方误传导致数据库/网络/序列化压力过大。
        if (size == null) {
            this.size = DEFAULT_PAGE_SIZE;
        } else if (size > MAX_PAGE_SIZE) {
            this.size = MAX_PAGE_SIZE;
        } else {
            this.size = size;
        }
    }

    public Long getCurrent() {
        // 页码从 1 开始；未传或非法时默认第一页。
        return this.current != null && this.current >= 1 ? this.current : 1;
    }

    @JsonIgnore
    public Long getPageTotal() {
        // total 或 size 不完整时无法计算页数，返回 0 表示“未知/不适用”。
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
        // SQL offset（从 0 开始），用于基于 page/size 的传统分页。
        return (this.getCurrent() - 1) * this.getSize();
    }

}
