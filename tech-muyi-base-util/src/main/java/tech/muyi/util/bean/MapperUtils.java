package tech.muyi.util.bean;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: MapperUtils
 * date: 2022/7/14 21:39
 * author: muyi
 * version: 1.0
 */
public enum MapperUtils {


    /**
     * 实例
     */
    ORIKA;

    /**
     * 默认字段工厂
     */
    private static final MapperFactory ORIKA_MAPPER_FACTORY;

    /**
     * 默认字段实例
     */
    private static final MapperFacade ORIKA_MAPPER_FACADE;

    /**
     * 默认字段实例集合
     */
    private static final Map<String, MapperFacade> ORIKA_CACHE_MAPPER_FACADE_MAP;

    static {
        ORIKA_MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();
        ORIKA_MAPPER_FACTORY.getConverterFactory().registerConverter(new JSONObjectConverter());
        ORIKA_MAPPER_FACADE = ORIKA_MAPPER_FACTORY.getMapperFacade();
        ORIKA_CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();
    }

    /**
     * 映射实体（默认字段）
     *
     * @param toClass 映射类对象
     * @param data    数据（对象）
     * @return 映射类对象
     */
    public <E, T> E map(Class<E> toClass, T data) {
        return ORIKA_MAPPER_FACADE.map(data, toClass);
    }

    /**
     * 映射实体（自定义配置）
     *
     * @param toClass   映射类对象
     * @param data      数据（对象）
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public <E, T> E map(Class<E> toClass, T data, Map<String, String> configMap) {
        MapperFacade mapperFacade = this.getMapperFacade(toClass, data.getClass(), configMap);
        return mapperFacade.map(data, toClass);
    }

    /**
     * 映射集合（默认字段）
     *
     * @param toClass 映射类对象
     * @param data    数据（集合）
     * @return 映射类对象
     */
    public <E, T> List<E> mapAsList(Class<E> toClass, Collection<T> data) {
        return ORIKA_MAPPER_FACADE.mapAsList(data, toClass);
    }


    /**
     * 映射集合（自定义配置）
     *
     * @param toClass   映射类
     * @param data      数据（集合）
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public <E, T> List<E> mapAsList(Class<E> toClass, Collection<T> data, Map<String, String> configMap) {
        T t = null;
        try {
            t = data.stream().findFirst().orElseThrow(() -> new Exception("映射集合，数据集合为空"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MapperFacade mapperFacade = this.getMapperFacade(toClass, t.getClass(), configMap);
        return mapperFacade.mapAsList(data, toClass);
    }

    /**
     * 获取自定义映射
     *
     * @param toClass   映射类
     * @param dataClass 数据映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private <E, T> MapperFacade getMapperFacade(Class<E> toClass, Class<T> dataClass, Map<String, String> configMap) {
        String mapKey = dataClass.getCanonicalName() + "_" + toClass.getCanonicalName();
        MapperFacade mapperFacade = ORIKA_CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            MapperFactory factory = new DefaultMapperFactory.Builder().build();
            ClassMapBuilder<T, E> classMapBuilder = factory.classMap(dataClass, toClass);
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = factory.getMapperFacade();
            ORIKA_CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

}