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
    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModified;
    /**
     * 行版本号
     */
    private Integer rowVersion;
    /**
     * 行状态
     */
    private Integer rowStatus;
    private Integer bizType;
    private String extAtt;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 修改人
     */
    private String operator;
}
