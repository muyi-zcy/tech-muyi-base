package tech.muyi.core.db.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tech.muyi.common.DTO.MyBaseDTO;

import java.io.Serializable;

/**
 * 用于接收前端列表查询及后端对查询的补充
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
            this.sort = StringUtils.upperCase(this.sort);
            return this.sort;
        }
    }

    public Boolean isPageSelect(){
        if(isPage == null || isPage){
            return true;
        }

        if(getCurrent() != null){
            return true;
        }

        return false;
    }

    public Boolean getIsSearchCount(){
        if(isSearchCount == null){
            return true;
        }
        return isSearchCount;
    }
}
