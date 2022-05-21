package tech.muyi.common.DTO;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:31
 */
@Data
public class MyBaseDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long id;
    private String creator;
    private String operator;
    private Integer bizType;
    private String extAtt;
}
