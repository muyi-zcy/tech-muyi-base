package tech.muyi.id;


/**
 * 生成唯一ID
 *
 * @Author: muyi
 * @Date: 2021/1/3 20:52
 */
public interface MyIdGenerator {
    void init();

    long nextId();

    String nextIdStr();
}
