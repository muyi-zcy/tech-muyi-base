package ${package.ServiceImpl};

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import javax.annotation.Resource;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import ${groupId}.core.entity.${entity}DO;
import ${groupId}.client.dto.${entity}DTO;
import ${groupId}.client.query.${entity}Query;
import ${groupId}.core.manager.${entity}Manager;
import ${groupId}.core.service.${entity}Service;
import tech.muyi.util.bean.MapperUtils;
import tech.muyi.id.MyIdGenerator;
import org.springframework.transaction.annotation.Transactional;
/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Slf4j
@Service
public class ${entity}ServiceImpl implements ${entity}Service {

    @Resource
    private ${entity}Manager ${entity?uncap_first}Manager;

    @Resource
    private MyIdGenerator myIdGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(${entity}DTO ${entity?uncap_first}DTO) {
        ${entity}DO ${entity?uncap_first}DO = MapperUtils.ORIKA.map(${entity}DO.class, ${entity?uncap_first}DTO);
        ${entity?uncap_first}DO.setId(myIdGenerator.nextId());
        return  ${entity?uncap_first}Manager.save(${entity?uncap_first}DO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(${entity}DTO ${entity?uncap_first}DTO) {
        ${entity}DO ${entity?uncap_first}DO;
        if(${entity?uncap_first}Manager.getById(${entity?uncap_first}DTO.getId()) == null){
            return false;
        }
        ${entity?uncap_first}DO = MapperUtils.ORIKA.map(${entity}DO.class, ${entity?uncap_first}DTO);
        return ${entity?uncap_first}Manager.update(${entity?uncap_first}DO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        return ${entity?uncap_first}Manager.deleteById(id);
    }

    @Override
    public ${entity}DTO getById(Long id) {

        ${entity}DO ${entity?uncap_first}DO = ${entity?uncap_first}Manager.getById(id);
        //对象转换
        ${entity}DTO ${entity?uncap_first}DTO = null;
        if(null != ${entity?uncap_first}DO){
            ${entity?uncap_first}DTO = MapperUtils.ORIKA.map(${entity}DTO.class, ${entity?uncap_first}DO);
        }
        return ${entity?uncap_first}DTO;
    }

    @Override
    public List<${entity}DTO> query(${entity}Query ${entity?uncap_first}Query) {
        List<${entity}DO> list = ${entity?uncap_first}Manager.pageSelect(${entity?uncap_first}Query);
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList();
        }
        List<${entity}DTO> resultList = MapperUtils.ORIKA.mapAsList(${entity}DTO.class,list);
        return resultList;
    }

}
