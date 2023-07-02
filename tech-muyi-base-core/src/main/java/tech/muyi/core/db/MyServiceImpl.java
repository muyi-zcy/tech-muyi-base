package tech.muyi.core.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import tech.muyi.core.db.query.MyQuery;

import java.util.List;

/**
 * @author: muyi
 * @date: 2023/7/3
 **/
@Slf4j
public class MyServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public List<T> pageQuery(MyQuery myBaseQuery){
        LambdaQueryWrapper<T> lambdaQueryWrapper = myBaseQuery.getLambdaQueryWrapper();
        IPage page = new Page<>(myBaseQuery.getCurrent(),myBaseQuery.getSize(), myBaseQuery.getIsSearchCount());
        this.page(page, lambdaQueryWrapper);
        MyQueryHelper.queryPageConfig(page,myBaseQuery);
        return page.getRecords();
    }

}
