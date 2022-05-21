package ${package.Service};


import ${groupId}.client.dto.${entity}DTO;
import ${groupId}.client.query.${entity}Query;
import java.util.*;
/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
public interface ${entity}Service {

    /**
     * 保存数据
     * @param ${entity?uncap_first}DTO
     * @return
     */
    Boolean save(${entity}DTO ${entity?uncap_first}DTO);

    /**
     * 根据ID逻辑删除数据
     * @param id
     * @return
     */
    Boolean delete(Long id);

    /**
     * 修改数据,必须带有ID
     * @param ${entity?uncap_first}DTO
     * @return
     */
    Boolean update(${entity}DTO ${entity?uncap_first}DTO);

    /**
     * 根据ID查询数据
     * @param id
     * @return
     */
    ${entity}DTO getById(Long id);

    /**
     * 根据查询条件获取数据
     * @param ${entity?uncap_first}Query
     * @return
     */
    List<${entity}DTO> query(${entity}Query ${entity?uncap_first}Query);

}
