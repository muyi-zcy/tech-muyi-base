package ${package.Service};


import tech.muyi.common.MyResult;
import java.util.*;

import ${groupId}.client.dto.${entity}DTO;
import ${groupId}.client.query.${entity}Query;

/**
* <p>
    * ${table.comment!} 
    * </p>
*
* @author ${author}
* @since ${date}
*/
public interface ${entity}ApiService {

    /**
     * 添加数据
     * @param ${entity?uncap_first}DTO
     * @return
     */
    MyResult<Boolean> save${entity}(${entity}DTO ${entity?uncap_first}DTO);

    /**
     * 添加数据
     * @param id
     * @return
     */
    MyResult<Boolean> delete${entity}(Long id);

    /**
     * 修改数据
     * @param ${entity?uncap_first}DTO
     * @return
     */
    MyResult<Boolean> update${entity}(${entity}DTO ${entity?uncap_first}DTO);

    /**
     * 获取数据
     * @param id
     * @return
     */
    MyResult<${entity}DTO> get${entity}ById(Long id);

    /**
     * 获取列表
     * @param ${entity?uncap_first}Query
     * @return
     */
    MyResult<List<${entity}DTO>> query${entity}(${entity}Query ${entity?uncap_first}Query);
}
