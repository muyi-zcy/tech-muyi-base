package tech.muyi.gengrator;

import java.util.Scanner;

public class MyGeneratorCode {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String projectName = "tech-muyi-user";
        System.out.println("请输入项目名："+projectName);

        String tableName = "t_user_info";
        System.out.println("表名："+tableName);

        String path = "D:\\muyi\\tech-muyi-example";
        System.out.println("项目地址："+path);

        String url= "124.222.183.53:3306";
        url = "jdbc:mysql://"+ url +"/"+projectName+"?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&serverTimezone=GMT%2B8";
        System.out.println("数据库地址："+url);

        String username= "root";
        System.out.println("数据库账户："+username);

        String password= "devMysqlPasswd";
        System.out.println("数据库密码："+password);

        String groupId = "tech.muyi";
        System.out.println("系统GroupId："+groupId );
        Controller.deal(projectName,tableName,path,url,username,password,groupId);
        Query.deal(projectName,tableName,path,url,username,password,groupId);
        Dto.deal(projectName,tableName,path,url,username,password,groupId);
        Dao.deal(projectName,tableName,path,url,username,password,groupId);
        Do.deal(projectName,tableName,path,url,username,password,groupId);
        Apiservice.deal(projectName,tableName,path,url,username,password,groupId);
        ApiServiceImpl.deal(projectName,tableName,path,url,username,password,groupId);
        Service.deal(projectName,tableName,path,url,username,password,groupId);
        ServiceImpl.deal(projectName,tableName,path,url,username,password,groupId);
        Mapper.deal(projectName,tableName,path,url,username,password,groupId);
        Manager.deal(projectName,tableName,path,url,username,password,groupId);
    }
}
