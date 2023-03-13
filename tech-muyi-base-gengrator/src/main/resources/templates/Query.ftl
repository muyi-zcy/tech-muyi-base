package ${package.Entity};

import lombok.experimental.Tolerate;
import tech.muyi.common.query.MyBaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;


/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@Builder
@ApiModel(value = "${entity}对象", description = "${table.comment!}")
public class ${entity} extends MyBaseQuery {

    private static final long serialVersionUID = 1L;

    @Tolerate
    public ${entity}() {}
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>


    @ApiModelProperty("${field.comment}")
    <#if field.keyFlag>
    <#-- 主键 -->
    @TableId(value = "${field.annotationColumnName}", type = IdType.AUTO)
    <#-- 普通字段 -->
    <#elseif field.convert>
    @TableField("${field.annotationColumnName}")
    </#if>
<#-- 乐观锁注解 -->
    <#if field.versionField>
    @Version
    </#if>
<#-- 逻辑删除注解 -->
    <#if field.logicDeleteField>
    @TableLogic
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->
}
