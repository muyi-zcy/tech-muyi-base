package tech.muyi.id;


/**
 * 生成唯一ID
 *
 * <p>统一 ID 生成器抽象，便于后续替换实现（雪花算法、号段模式等）而不影响调用方。</p>
 *
 * @Author: muyi
 * @Date: 2021/1/3 20:52
 */
public interface MyIdGenerator {
    void init();

    long nextId();

    String nextIdStr();
}
