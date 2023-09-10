package tech.muyi.common.DO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: muyi
 * @Date: 2021/1/3 21:30
 */
@Data
public class MyBaseDO implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Long id;
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime gmtModified;

    @Version
    protected Integer rowVersion;

    @TableLogic
    protected Integer rowStatus;

    protected Integer bizType;

    protected String extAtt;
    @TableField(fill = FieldFill.INSERT)
    protected String creator;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String operator;
    private String tenantId;
}
