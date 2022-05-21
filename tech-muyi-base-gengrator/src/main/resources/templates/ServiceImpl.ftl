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

    @Override
    public Boolean save(${entity}DTO ${entity?uncap_first}DTO) {
        ${entity}DO ${entity?uncap_first}DO = new ${entity}DO();
        BeanUtils.copyProperties(${entity?uncap_first}DTO,${entity?uncap_first}DO);
        return  ${entity?uncap_first}Manager.save(${entity?uncap_first}DO);
    }

    @Override
    public Boolean update(${entity}DTO ${entity?uncap_first}DTO) {
        ${entity}DO ${entity?uncap_first}DO;
        if((${entity?uncap_first}DO = ${entity?uncap_first}Manager.getById(${entity?uncap_first}DTO.getId())) == null){
            return false;
        }
        BeanUtils.copyProperties(${entity?uncap_first}DTO, ${entity?uncap_first}DO);
        return ${entity?uncap_first}Manager.update(${entity?uncap_first}DO);
    }

    @Override
    public Boolean delete(Long id) {
        return ${entity?uncap_first}Manager.deleteById(id);
    }

    @Override
    public ${entity}DTO getById(Long id) {

        ${entity}DO ${entity?uncap_first}DO = ${entity?uncap_first}Manager.getById(id);
        //对象转换
        ${entity}DTO ${entity?uncap_first}DTO = null;
        if(null != ${entity?uncap_first}DO){
            ${entity?uncap_first}DTO = new ${entity}DTO();
            BeanUtils.copyProperties(${entity?uncap_first}DO,${entity?uncap_first}DTO);
        }
        return ${entity?uncap_first}DTO;
    }

    @Override
    public List<${entity}DTO> query(${entity}Query ${entity?uncap_first}Query) {
        List<${entity}DO> list = ${entity?uncap_first}Manager.query(${entity?uncap_first}Query);
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList();
        }
        if(null == ${entity?uncap_first}Query.getTotal()){
            Long cnt = ${entity?uncap_first}Manager.queryCount(${entity?uncap_first}Query);
            ${entity?uncap_first}Query.setTotal(cnt == null ? 0 : cnt);
        }
        List<${entity}DTO> resultList = list.stream()
                .map(d -> {
                    ${entity}DTO dto = new ${entity}DTO();
                    BeanUtils.copyProperties(d, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        return resultList;
    }

}
