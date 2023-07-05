package ${package.Entity};

import lombok.experimental.Tolerate;
import tech.muyi.core.db.query.MyQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import tech.muyi.common.DTO.MyBaseDTO;


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
public class ${entity} extends MyQuery<MyBaseDTO> {

    private static final long serialVersionUID = 1L;

    @Tolerate
    public ${entity}() {}
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>
    @ApiModelProperty("${field.comment}")
    private ${field.propertyType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->
}
