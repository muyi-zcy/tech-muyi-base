package ${package.Service};

import tech.muyi.common.MyResult;
import java.util.*;
import ${groupId}.client.dto.${entity}DTO;
import ${groupId}.client.query.${entity}Query;
import ${groupId}.core.service.${entity}Service;
import ${groupId}.client.api.${entity}ApiService;
import org.apache.dubbo.config.annotation.DubboService;
import tech.muyi.exception.UnknownException;
import org.springframework.beans.factory.annotation.Autowired;
import tech.muyi.exception.MyException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Slf4j
@DubboService(version = "1.0.0", timeout = 5000)
public class ${entity}ApiServiceImpl implements ${entity}ApiService {

    @Autowired
    private ${entity}Service ${entity?uncap_first}Service;

    /**
     * 添加
     * @param ${entity?uncap_first}DTO
     * @return
     */
    @Override
    public MyResult<Boolean> save${entity}(${entity}DTO ${entity?uncap_first}DTO){
        try{
            Boolean cnt = ${entity?uncap_first}Service.save(${entity?uncap_first}DTO);
            return MyResult.ok(cnt);
        }catch (MyException e){
            log.error("save${entity} MyException,{}",${entity?uncap_first}DTO,e);
            return MyResult.fail(e);
        }catch (Exception e){
            log.error("save${entity} exception,{}",${entity?uncap_first}DTO,e);
            return MyResult.fail(new UnknownException(e));
        }
    }


    /**
    * 删除
    * @return
    */
    @Override
    public MyResult<Boolean> delete${entity}(Long id){
        try{
            Boolean cnt = ${entity?uncap_first}Service.delete(id);
            return MyResult.ok(cnt);
        }catch (MyException e){
            log.error("delete${entity} MyException,{}",id,e);
            return MyResult.fail(e);
        }catch (Exception e){
            log.error("delete${entity} exception,{}",id,e);
            return MyResult.fail(new UnknownException(e));
        }
    }

    /**
    * 修改
    * @param ${entity?uncap_first}DTO
    * @return
    */
    @Override
    public MyResult<Boolean> update${entity}(${entity}DTO ${entity?uncap_first}DTO){
        try{
            Boolean cnt = ${entity?uncap_first}Service.update(${entity?uncap_first}DTO);
            return MyResult.ok(cnt);
        }catch (MyException e){
            log.error("update${entity} MyException,{}",${entity?uncap_first}DTO,e);
            return MyResult.fail(e);
        }catch (Exception e){
            log.error("update${entity} exception,{}",${entity?uncap_first}DTO,e);
            return MyResult.fail(new UnknownException(e));
        }
    }

    /**
    * 根据ID查询
    * @param id
    * @return
    */
    @Override
    public MyResult<${entity}DTO> get${entity}ById(Long id){
        try{
            ${entity}DTO ${entity?uncap_first}DTO = ${entity?uncap_first}Service.getById(id);
            return MyResult.ok(${entity?uncap_first}DTO);
        }catch (MyException e){
            log.error("get${entity}ById MyException,{}",id,e);
            return MyResult.fail(e);
        }catch (Exception e){
            log.error("get${entity}ById exception,{}",id,e);
            return MyResult.fail(new UnknownException(e));
        }
    }

    /**
    *
    * @param ${entity?uncap_first}Query 查询条件
    * @return
    */
    @Override
    public MyResult<List<${entity}DTO>> query${entity}(${entity}Query ${entity?uncap_first}Query){
        try{
            List<${entity}DTO> list = ${entity?uncap_first}Service.query(${entity?uncap_first}Query);
            return MyResult.ok(list, ${entity?uncap_first}Query);

        } catch (MyException e){
            log.error("query${entity} MyException,{}",${entity?uncap_first}Query,e);
            return MyResult.fail(e);
        } catch (Exception e){
            log.error("query${entity} exception,{}",${entity?uncap_first}Query,e);
            return MyResult.fail(new UnknownException(e));
        }
    }
}
