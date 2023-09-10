package tech.muyi.common.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:31
 */
@Data
public class MyBaseDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("id")
    protected Long id;

    @ApiModelProperty("创建时间")
    protected LocalDateTime gmtCreate;

    @ApiModelProperty("修改时间")
    protected LocalDateTime gmtModified;

    @ApiModelProperty("业务类型")
    protected Integer bizType;

    @ApiModelProperty("拓展内容")
    protected String extAtt;

    @ApiModelProperty("创建人")
    protected String creator;

    @ApiModelProperty("修改人")
    protected String operator;
}
