package tech.muyi.core.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * DAO 基础接口（对 {@link BaseMapper} 的语义化封装）。
 *
 * <p>目的：统一项目内 DAO 命名与继承结构，便于后续做全局扩展（例如统一方法、拦截器约定等）。</p>
 *
 * @Author: muyi
 * @Date: 2021/1/10 22:29
 */
public interface MyBaseDAO<T> extends BaseMapper<T>{

}
