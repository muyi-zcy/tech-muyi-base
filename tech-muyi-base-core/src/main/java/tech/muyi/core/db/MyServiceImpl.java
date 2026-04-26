package tech.muyi.core.db;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import tech.muyi.core.db.query.MyQuery;

import java.util.List;

/**
 * 基于 MyBatis-Plus {@link ServiceImpl} 的通用扩展：
 * - 提供统一的分页查询入口（对接 {@link MyQuery} + {@link MyQueryHelper}）
 * - 提供便捷的 update(Wrapper) 重载，避免每次都 new 实体
 *
 * <p>说明：该基类不强行规定业务层规则，仅做基础能力复用；具体查询条件解析由 {@link MyQueryHelper} 负责。</p>
 *
 * @author: muyi
 * @date: 2023/7/3
 **/
@Slf4j
public abstract class MyServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {


    public  abstract T createEntity();

    public List<T> pageQuery(MyQuery myBaseQuery) {
        // wrapper 由上层提前构建（通常通过 MyQueryHelper.createQueryWrapper）并缓存到 MyQuery 中。
        LambdaQueryWrapper<T> lambdaQueryWrapper = myBaseQuery.getLambdaQueryWrapper();
        // isSearchCount 控制是否查询总数：大表/深分页下 count 可能很慢，允许调用方关闭。
        IPage page = new Page<>(myBaseQuery.getCurrent(), myBaseQuery.getSize(), myBaseQuery.getIsSearchCount());
        this.page(page, lambdaQueryWrapper);
        // 将分页结果元数据回填到 query 对象，便于统一返回给前端（current/size/total 等）。
        MyQueryHelper.queryPageConfig(page, myBaseQuery);
        return page.getRecords();
    }

    public boolean update(Wrapper<T> updateWrapper) {
        // 统一用空实体触发 update wrapper 语义（MP 需要实体对象作为入参，即便实际只用 wrapper 条件）。
        return update(createEntity(), updateWrapper);
    }
}
