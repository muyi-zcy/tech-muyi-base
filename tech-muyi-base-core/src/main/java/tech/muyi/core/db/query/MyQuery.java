package tech.muyi.core.db.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.DTO.MyBaseDTO;

import java.io.Serializable;

/**
 * 用于接收前端列表查询及后端对查询的补充
 *
 * <p>约定：
 * <ul>
 *   <li>带 {@link JsonIgnore} 的字段不参与 JSON 协议（通常由后端内部补充/控制）。</li>
 *   <li>分页能力同时支持“传统 page/size”与“游标 lastId”两种模式；由上层根据业务选择。</li>
 * </ul>
 *
 *
 * @Author: muyi
 * @Date: 2021/1/3 21:19
 */
@Data
public class MyQuery<T extends MyBaseDTO> extends AbstractQuery<T> implements Serializable {

    // 排序
    @JsonIgnore
    private String sort;

    // 查询起始id
    @JsonIgnore
    private Long lastId;

    // 是否查询总数
    @JsonIgnore
    private Boolean isSearchCount = true;

    // 是否分页
    @JsonIgnore
    private Boolean isPage;


    public String getSort() {
        if (StringUtils.isEmpty(this.sort)) {
            return this.sort;
        } else {
            // sort 统一转大写，便于后续 switch/枚举匹配，不要求调用方严格大小写。
            this.sort = StringUtils.upperCase(this.sort);
            return this.sort;
        }
    }

    public Boolean isPageSelect(){
        // 默认分页：isPage 未指定或为 true 时认为需要分页。
        if(isPage == null || isPage){
            return true;
        }

        // 兼容老调用：只要传了 current，就认为走分页查询（即使 isPage 显式为 false）。
        if(getCurrent() != null){
            return true;
        }

        return false;
    }

    public Boolean getIsSearchCount(){
        // 默认查询总数：调用方不传则按 true，避免出现“分页无总数”影响前端分页控件。
        if(isSearchCount == null){
            return true;
        }
        return isSearchCount;
    }
}
