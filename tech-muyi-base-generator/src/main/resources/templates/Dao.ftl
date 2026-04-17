package ${package.Service};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${groupId}.core.entity.${entity}DO;
/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Mapper
public interface ${entity}DAO  extends BaseMapper<${entity}DO> {

}