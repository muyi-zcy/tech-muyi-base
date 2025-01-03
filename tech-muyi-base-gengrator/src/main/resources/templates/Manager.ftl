package ${package.Service};


import ${groupId}.client.query.${entity}Query;
import ${groupId}.core.dao.${entity}DAO;
import ${groupId}.core.entity.${entity}DO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import tech.muyi.common.constant.enumtype.RowStatusEnum;
import tech.muyi.core.db.MyQueryHelper;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import tech.muyi.util.bean.MapperUtils;
import tech.muyi.core.db.MyServiceImpl;


/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Component
public class ${entity}Manager extends MyServiceImpl<${entity}DAO, ${entity}DO>{

    @Resource
    private ${entity}DAO ${entity?uncap_first}DAO;

    public Boolean deleteById(Long id){
        return this.removeById(id);
    }

    public Boolean update(${entity}DO ${entity?uncap_first}DO) {
        LambdaUpdateWrapper<${entity}DO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(${entity}DO::getId,${entity?uncap_first}DO.getId());
        return this.update(${entity?uncap_first}DO,updateWrapper);
    }

    public List<${entity}DO> query(${entity}Query ${entity?uncap_first}Query) {
        LambdaQueryWrapper<${entity}DO> lambdaQueryWrapper = MyQueryHelper.createQueryWrapper(${entity?uncap_first}Query);
        ${entity}DO ${entity?uncap_first}DO = MapperUtils.ORIKA.map(${entity}DO.class, ${entity?uncap_first}Query);
        lambdaQueryWrapper.setEntity(${entity?uncap_first}DO);
        return this.list(lambdaQueryWrapper);
    }

    public List<${entity}DO> pageQuery(${entity}Query ${entity?uncap_first}Query){
        LambdaQueryWrapper<${entity}DO> lambdaQueryWrapper = MyQueryHelper.createQueryWrapper(${entity?uncap_first}Query);
        ${entity}DO ${entity?uncap_first}DO = MapperUtils.ORIKA.map(${entity}DO.class, ${entity?uncap_first}Query);
        lambdaQueryWrapper.setEntity(${entity?uncap_first}DO);
        return super.pageQuery(${entity?uncap_first}Query);
    }

    public Long queryCount(${entity}Query ${entity?uncap_first}Query) {
        LambdaQueryWrapper<${entity}DO> lambdaQueryWrapper = MyQueryHelper.createQueryWrapper(${entity?uncap_first}Query);
        ${entity}DO ${entity?uncap_first}DO = MapperUtils.ORIKA.map(${entity}DO.class, ${entity?uncap_first}Query);
        lambdaQueryWrapper.setEntity(${entity?uncap_first}DO);
        return this.count(lambdaQueryWrapper);
    }
}