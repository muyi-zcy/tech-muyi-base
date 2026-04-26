package tech.muyi.gengrator.convert;

import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * 动态列类型描述。
 *
 * <p>用于在运行时构造 IColumnType，支持自定义类型与包路径。</p>
 *
 * @author: muyi
 * @date: 2023/3/2
 **/
public class MyDynomicColumnType implements IColumnType {
    private String type;
    private String pkg;

    public MyDynomicColumnType(String type, String pkg) {
        this.type = type;
        this.pkg = pkg;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getPkg() {
        return this.pkg;
    }
}
