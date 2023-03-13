package tech.muyi.gengrator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    public static void deal(String projectName,String tableName,String path,String url,String username,String password,String groupId){
        Map<String,Object> custom = new HashMap<>();
        custom.put("groupId",groupId);
        custom.put("apiName",projectName);
        FastAutoGenerator.create(url,username, password)
                .globalConfig(builder -> {
                    builder.author(System.getProperty("user.name")) // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .dateType(DateType.TIME_PACK)
                            .outputDir(path +"/"+projectName + "/" +projectName+"-web"+"/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(groupId) // 设置父包名
                            .moduleName("web"); // 设置父包模块名
                })
                .templateConfig(builder -> {
                    builder.controller("/templates/Controller")
                            .disable(TemplateType.ENTITY,TemplateType.MAPPER,TemplateType.SERVICE,TemplateType.SERVICEIMPL,TemplateType.XML,TemplateType.MAPPER);;
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableName) // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .injectionConfig(builder -> {
                    builder.customMap(custom).build();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
