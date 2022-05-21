package tech.muyi.util.request;

import cn.hutool.json.JSONUtil;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: muyi
 * @Date: 2021/1/26 19:42
 */
public class RequestCollectUtil {

    private static final String NULL_AUTH = "NULL_AUTH_@@##$$";

    /**
     * 默认参数，全局下所有携带某注解的请求路径
     * @return
     */
    public static Set<String> getRequestCollect(){
        return getRequestBase(null,new String[]{});
    }

    public static Set<String> getRequestCollect(String packageName, String tags){
        return getRequestBase(new String[]{packageName},new String[]{tags});
    }

    public static Set<String> getRequestCollect(String[] packageName, String[] tags){
        return getRequestBase(packageName,tags);
    }

    public static Set<String> getRequestCollect(String packageName, String[] tags){
        return getRequestBase(new String[]{packageName},tags);
    }

    public static Set<String> getRequestCollect(String[] packageName, String tags){
        return getRequestBase(packageName,new String[]{tags});
    }

    /**
     * 默认参数，默认包下所有携带注解的请求路径
     * @return
     */
    public static Set<String> getRequestCollectWithPackage(String... packageName){
        return getRequestBase(packageName,new String[]{});
    }

    /**
     * 全局包下携带注解某些tag的请求路径
     * @param tags
     * @return
     */
    public static Set<String> getRequestCollectWithTags(String... tags){
        return getRequestBase(null,tags);
    }


    private static Set<String> getRequestBase(String[] packageName, String[] tags){
        if(packageName == null || packageName.length == 0){
            packageName = new String[]{getFirstPackage()};
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packageName) // 指定路径URL
                .addScanners(new MethodAnnotationsScanner()) // 添加 方法注解扫描工具
                .addScanners(new FieldAnnotationsScanner()) // 添加 属性注解扫描工具
        );



        Set<Class<?>> restControllerClassSet = reflections.getTypesAnnotatedWith(RestController.class);

        Set<Class<?>> requestCollectClassSet = reflections.getTypesAnnotatedWith(RequestCollect.class);

        Set<String> resultSet = new HashSet<>();
        for(Class c : restControllerClassSet){
            String classMapperUrl = getAuthAnnotationValue(c.getAnnotations(),tags);
            if(NULL_AUTH.equals(classMapperUrl)){
                continue;
            }
            //判断类上是否携带注解
            if(requestCollectClassSet.contains(c)){ //获取类内所有方法
                Method[] methods = c.getMethods();
                for(int i = 0;i<methods.length;i++){
                    Annotation[] annotations = methods[i].getAnnotations();
                    String url = getAuthAnnotationValue(annotations,tags);
                    if(NULL_AUTH.equals(url) || "".equals(url) || url.isEmpty()){
                        continue;
                    }
                    resultSet.add(classMapperUrl+url);
                }
            }else {

                Method[] methods = c.getMethods();
                for(Method method : methods){
                    Annotation[] annotations = method.getAnnotations();
                    String url = getPublicAnnotationValue(annotations,tags);
                    if(NULL_AUTH.equals(url) || "".equals(url) || url.isEmpty()){
                        continue;
                    }
                    resultSet.add(classMapperUrl+url);
                }
            }
        }
        return resultSet;
    }

    private static String getAuthAnnotationValue(Annotation[] annotations, String[] tags){
        String url = "";
        for (int i = 0; i < annotations.length; i++) {
            Class value = annotations[i].annotationType();
            if(value.equals(RequestCollect.class)) {
                RequestCollect requestCollect  = (RequestCollect)annotations[i];
                String tagsString = requestCollect.value();
                String[] classTags = tagsString.split("\\,");
                if(!inAuth(classTags,tags)){
                    return NULL_AUTH;
                }
            }
            String s = getMapperValue(annotations[i]);
            if(!"".equals(s)){
                url = s;
            }
        }
        return url;
    }

    private static String getPublicAnnotationValue(Annotation[]  annotations, String[] tags){
        String url = "";
        boolean flag  = false;
        for (int i = 0; i < annotations.length; i++) {
            Class value = annotations[i].annotationType();
            if(value.equals(RequestCollect.class)) {
                flag = true;
                RequestCollect requestCollect  = (RequestCollect)annotations[i];
                String tagsString = requestCollect.value();
                String[] classTags = tagsString.split("\\,");
                if(!inAuth(classTags,tags)){
                    return NULL_AUTH;
                }
            }
            String s = getMapperValue(annotations[i]);
            if(!"".equals(s)){
                url = s;
            }
        }
        return flag? url : NULL_AUTH;
    }

    private static boolean inAuth(String[] classTags, String[] tags){
        for(int i=0; i < tags.length; i++){
            boolean flag = false;
            for(int j = 0; j < classTags.length; j++ ){
                if(tags[i].equals(classTags[j])){
                    flag = true;
                }
            }
            if(flag == false){
                return false;
            }
        }
        return true;
    }

    private static String getMapperValue(Annotation annotation){
        String value = "";
        if (annotation.annotationType().equals(RequestMapping.class)) {
            RequestMapping requestMapping = (RequestMapping)annotation;
            value += requestMapping.value().length == 0 ? "" : requestMapping.value()[0].toString();
        }
        if (annotation.annotationType().equals(GetMapping.class)) {
            GetMapping getMapping = (GetMapping) annotation;
            value += getMapping.value().length == 0 ? "" : getMapping.value()[0].toString();
        }
        if (annotation.annotationType().equals(PutMapping.class)) {
            PutMapping putMapping = (PutMapping) annotation;
            value += putMapping.value().length == 0 ? "" : putMapping.value()[0].toString();
        }
        if (annotation.annotationType().equals(PostMapping.class)) {
            PostMapping postMapping = (PostMapping) annotation;
            value += postMapping.value().length == 0 ? "" : postMapping.value()[0].toString();
        }
        if (annotation.annotationType().equals(DeleteMapping.class)) {
            DeleteMapping deleteMapping = (DeleteMapping) annotation;
            value += deleteMapping.value().length == 0 ? "" : deleteMapping.value()[0].toString();
        }
        return value;
    }

    /**
     * 获得最外层的包路径
     * @return
     */
    public static String getFirstPackage(){
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement targetElement = stackTrace[2];
            //类名
            String className = targetElement.getClassName();
            return className.split("\\.")[0];
        }catch (Exception e){
            return "";
        }
    }


    public static void main(String[] args) {
        System.out.println(JSONUtil.toJsonStr(getRequestCollect()));
    }
}
