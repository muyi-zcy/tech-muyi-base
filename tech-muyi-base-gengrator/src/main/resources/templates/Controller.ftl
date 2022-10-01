package ${package.Controller};

import io.swagger.annotations.ApiOperation;
import tech.muyi.common.MyResult;
import lombok.extern.slf4j.Slf4j;
import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${groupId}.client.dto.${entity}DTO;
import ${groupId}.client.query.${entity}Query;
import ${groupId}.core.service.${entity}Service;

/**
* <p>
    * ${table.comment!} 前端控制器
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Slf4j
@RestController
@RequestMapping("${apiName}/${table.entityPath}")
public class ${table.controllerName} {
    @Autowired
    private ${entity}Service ${entity?uncap_first}Service;


    /**
     * 添加
     * @param ${entity?uncap_first}DTO
     * @return
     */
    @ApiOperation(value = "保存")
    @PostMapping("/")
    @ResponseBody
    public MyResult<Boolean> save${entity}(@RequestBody ${entity}DTO ${entity?uncap_first}DTO){

        if(${entity?uncap_first}DTO == null){
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), CommonErrorCodeEnum.INVALID_PARAM.getResultMsg());
        }

        Boolean cnt = ${entity?uncap_first}Service.save(${entity?uncap_first}DTO);
        return MyResult.ok(cnt);

    }


    /**
    * 删除
    * @return
    */
    @ApiOperation(value = "删除")
    @DeleteMapping("/{id}")
    @ResponseBody
    public MyResult<Boolean> delete${entity}(@PathVariable Long id){

        if(id == null || id < 1){
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), CommonErrorCodeEnum.INVALID_PARAM.getResultMsg());
        }

        Boolean cnt = ${entity?uncap_first}Service.delete(id);
        return MyResult.ok(cnt);

    }

    /**
    * 修改
    * @param ${entity?uncap_first}DTO
    * @return
    */
    @ApiOperation(value = "更新")
    @PutMapping("/")
    @ResponseBody
    public MyResult<Boolean> update${entity}(@RequestBody ${entity}DTO ${entity?uncap_first}DTO){

        if(${entity?uncap_first}DTO == null || ${entity?uncap_first}DTO.getId() == null){
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), CommonErrorCodeEnum.INVALID_PARAM.getResultMsg());
        }

        Boolean cnt = ${entity?uncap_first}Service.update(${entity?uncap_first}DTO);
        return MyResult.ok(cnt);

    }


    /**
    * 根据ID查询
    * @param id
    * @return
    */
    @ApiOperation(value = "详情查询")
    @GetMapping("/{id}")
    @ResponseBody
    public MyResult<${entity}DTO> get${entity}ById(@PathVariable Long id){

        if(id == null || id < 1){
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), CommonErrorCodeEnum.INVALID_PARAM.getResultMsg());
        }

        ${entity}DTO ${entity?uncap_first}DTO = ${entity?uncap_first}Service.getById(id);
        return MyResult.ok(${entity?uncap_first}DTO);

    }

    /**
    *
    * @param ${entity?uncap_first}Query 查询条件，根据业务自行扩展
    * @return
    */
    @ApiOperation(value = "分页查询")
    @GetMapping("/query")
    @ResponseBody
    public MyResult<List<${entity}DTO>> query${entity}(${entity}Query ${entity?uncap_first}Query ){

        if(${entity?uncap_first}Query == null){
            throw new MyException(CommonErrorCodeEnum.INVALID_PARAM.getResultCode(), CommonErrorCodeEnum.INVALID_PARAM.getResultMsg());
        }
        //分页
        List<${entity}DTO> list = ${entity?uncap_first}Service.query(${entity?uncap_first}Query);
        return MyResult.ok(list,${entity?uncap_first}Query);

    }
}