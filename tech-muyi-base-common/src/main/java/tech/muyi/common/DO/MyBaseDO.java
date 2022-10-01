package tech.muyi.common.DO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:30
 */
@Data
public class MyBaseDO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer rowVersion;
    private Integer rowStatus;
    private Integer bizType;
    private String extAtt;
    private String creator;
    private String operator;
}
