package tech.muyi.common.interfacesignature;

import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.muyi.common.MyResult;
import tech.muyi.common.interfacesignature.annotation.InterfaceSignature;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: muyi
 * @Date: 2021/1/16 0:57
 */
@Aspect
@Component
public class InterfaceSignatureAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(InterfaceSignatureAspect.class);

    public static final Integer[] WEIGHT = {7, 9, 10 ,5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};


    public static final String SIGN = "SIGN";

    @Value("auth.sign.public_key:default")
    public String PUBLIC_KEY;

    @Value("auth.sign.private_key:default")
    public String PRIVATE_KEY;

    @Value("auth.sign.ras_salt:default")
    public String RAS_SALT;

    public static final String REQUEST_METHON = "http_method";

    public static final String IP = "ip";

    public static final String URL = "url";

    public static final String CLASS_METHOD = "class_method";

    public static final String PARAMS = "params";

    @Pointcut("@annotation(tech.muyi.common.interfacesignature.annotation.InterfaceSignature)")
    public void pointCut(){

    }

    @Before("pointCut()")
    public void doBefore(JoinPoint joinPoint) {

    }


    @After("pointCut()")
    public void doAfter() throws Throwable{

    }

    @AfterReturning(returning = "ret",pointcut = "pointCut()")
    public void doAfterReturning(Object ret) throws Throwable{

    }

    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) throws Throwable{

    }


    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Object result = proceedingJoinPoint.proceed();
        try {
            if(checkSignature(proceedingJoinPoint)){
                return result;
            }
        }catch (Exception e){
            return MyResult.fail(CommonErrorCodeEnum.SERVER_BUSY.getResultCode(),CommonErrorCodeEnum.SERVER_BUSY.getResultMsg());
        }
        return MyResult.fail(CommonErrorCodeEnum.SIGNATURE_ERROR.getResultCode(),CommonErrorCodeEnum.SIGNATURE_ERROR.getResultMsg());
    }



    public boolean checkSignature(JoinPoint joinPoint){
        long beginTime = System.currentTimeMillis();

        Map map = getParam(joinPoint);

        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            Method method = methodSignature.getMethod();
            InterfaceSignature interfaceSignature = method.getAnnotation(InterfaceSignature.class);

            String ip = map.get(IP).toString();
            String requestMethod = map.get(REQUEST_METHON).toString();
            String methodName = map.get(CLASS_METHOD).toString();

            String module = interfaceSignature.module();
            Integer function = interfaceSignature.function().getCode();
            Integer appSource = interfaceSignature.appSource().getCode();

            String[] params = weightSort((Object[]) map.get(PARAMS));
            String sign = map.get(SIGN).toString();


            if(params != null && params.length>0){
                int length = params.length;
                StringBuilder stringBuilder = new StringBuilder(length);
                for(int i =0; i< length; i++) {
                    if(StringUtils.isBlank(params[i].toString())){
                        if(i == length-1){
                            stringBuilder.append(params[i]);
                        }else {
                            stringBuilder.append(params[i]+"&");
                        }
                    }
                }

                Sign targetSign = SecureUtil.sign(SignAlgorithm.SHA256withRSA);
                byte[] data = stringBuilder.toString().getBytes();
                byte[] bytes = targetSign.sign(data);
                return targetSign.verify(data,bytes);
            }

            LOGGER.info("接口签名,请求来源:[{}],方法:[{}],操作:[{}],请求参数[{}]",
                    appSource,methodName,function,params);
            return true;
        }catch (Exception e){
            LOGGER.error("接口校验失败,请求参数=[{}]",map);
            return false;
        }finally {
            long endTime = System.currentTimeMillis();
            LOGGER.info("接口签名,处理时长:[{}]",endTime - beginTime);
        }
    }



    public Map<String,Object> getParam(JoinPoint joinPoint){
        Map<String,Object> param = new HashMap<>(6);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        String url = request.getRequestURL().toString();

        String httpMethod = request.getMethod();

        String ip = request.getRemoteAddr();

        String function = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        JSONObject jsonObject = (JSONObject)args[0];
        Object[] objects = new Object[jsonObject.size()];

        Iterator iterator = jsonObject.entrySet().iterator();

        int j = 0;
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            if(SIGN.equals(key)){
                param.put(key,value);
                objects[j] = "rasSalt="+RAS_SALT;
            }else {
                objects[j] = key +"=" +entry.getValue().toString();
            }
            j++;
        }


        param.put(URL,url);
        param.put(IP,ip);
        param.put(REQUEST_METHON,httpMethod);
        param.put(CLASS_METHOD,function);
        param.put(PARAMS,objects);

        return param;
    }


    /**
     * weight dictionary sort
     * @param param
     * @return
     */
    public static String[] weightSort(Object[] param){
        String[] sortParam = new String[param.length];

        //Dictionary sequence
        Arrays.sort(param);

        Map<String,Object> hashMap = new HashMap<>();
        Object[] sortObject = new Object[param.length];

        for(int i = 0; i < param.length; i++){
            String s = String.valueOf(NumberUtil.mul(new Double(HashUtil.mixHash((String) param[i])),new Double(WEIGHT[i%WEIGHT.length])))+ RandomUtil.randomString(6);
            hashMap.put(s,param[i]);
            sortObject[i] = s;
        }

        Arrays.sort(sortObject);

        for(int i = 0; i< sortObject.length; i++){
            sortParam[i] = hashMap.get(sortObject[i]).toString();
        }

        return sortParam;
    }
}
