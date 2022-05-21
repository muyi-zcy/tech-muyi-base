package tech.muyi.gengrator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class ServiceImpl {

    public static void deal(String projectName,String tableName,String path,String url,String username,String password,String groupId){
        Map<String,Object> custom = new HashMap<>();
        custom.put("groupId",groupId);
        FastAutoGenerator.create(url,username, password)
                .globalConfig(builder -> {
                    builder.author(System.getProperty("user.name")) // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .dateType(DateType.ONLY_DATE)
                            .outputDir(path +"/"+projectName + "/" +projectName+"-core"+"/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(groupId) // 设置父包名
                            .moduleName("core")
                            .entity("service.impl"); // 设置父包模块名
                })
                .templateConfig(builder -> {
                    builder.serviceImpl("templates/ServiceImpl")
                            .disable(TemplateType.ENTITY,TemplateType.MAPPER,TemplateType.CONTROLLER,TemplateType.SERVICE,TemplateType.XML,TemplateType.MAPPER);
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableName) // 设置需要生成的表名
                            .addTablePrefix("t_", "c_")
                            .serviceBuilder()
                            .formatServiceFileName("%sServiceImpl")
                            .build(); // 设置过滤表前缀
                })
                .injectionConfig(builder -> {
                    builder.customMap(custom).build();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
