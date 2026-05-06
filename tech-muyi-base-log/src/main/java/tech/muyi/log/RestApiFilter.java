package tech.muyi.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tech.muyi.common.MyResult;
import tech.muyi.util.MyJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST 接口日志切面。
 *
 * <p>在控制器方法前后记录请求参数、返回结果与耗时，便于线上问题排查。
 * 会主动跳过健康检查路径 `/ok`，减少无意义日志噪音。</p>
 *
 * @Author: muyi
 * @Date: 2021/1/4 23:15
 */
@Aspect
@Component
public class RestApiFilter {
    /** JSON 日志最大长度，超过此长度将被截断 */
    private static final int MAX_JSON_LOG_LENGTH = 2000;
    /** 集合/数组最大元素数量，超过则跳过序列化 */
    private static final int MAX_COLLECTION_SIZE = 100;
    /** 字符串最大长度，超过则跳过序列化 */
    private static final int MAX_STRING_LENGTH = 5000;

    public RestApiFilter() {
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restApiAspect() {
    }

    /**
     * 截断过长的 JSON 字符串，防止打爆日志。
     *
     * @param json 原始 JSON 字符串
     * @return 截断后的 JSON 字符串，超过最大长度时会添加省略标记
     */
    private String truncateJson(String json) {
        if (json == null) {
            return "null";
        }
        if (json.length() <= MAX_JSON_LOG_LENGTH) {
            return json;
        }
        return json.substring(0, MAX_JSON_LOG_LENGTH) + "...(truncated, total length: " + json.length() + ")";
    }

    /**
     * 判断对象是否过大，避免序列化大对象浪费内存。
     *
     * @param obj 待判断的对象
     * @return 如果对象过大返回描述信息，否则返回 null
     */
    private String checkLargeObject(Object obj) {
        if (obj == null) {
            return null;
        }

        // 检查集合类型
        if (obj instanceof java.util.Collection) {
            java.util.Collection<?> collection = (java.util.Collection<?>) obj;
            if (collection.size() > MAX_COLLECTION_SIZE) {
                return "[large collection, size=" + collection.size() + ", skipped]";
            }
        }

        // 检查数组类型
        if (obj.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(obj);
            if (length > MAX_COLLECTION_SIZE) {
                return "[large array, length=" + length + ", skipped]";
            }
        }

        // 检查字符串类型
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.length() > MAX_STRING_LENGTH) {
                return "[large string, length=" + str.length() + ", skipped]";
            }
        }

        // 检查 Map 类型
        if (obj instanceof java.util.Map) {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) obj;
            if (map.size() > MAX_COLLECTION_SIZE) {
                return "[large map, size=" + map.size() + ", skipped]";
            }
        }

        return null;
    }

    @Around("restApiAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if ("/ok".equals(request.getRequestURI())) {
            return joinPoint.proceed();
        } else {
            Object target = joinPoint.getTarget();
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            String[] parameterNames = methodSignature.getParameterNames();
            Logger logger = LoggerFactory.getLogger(target.getClass());

            long startTime;
            StringBuilder logMsg;

            try {
                logMsg = new StringBuilder();
                logMsg.append("url:")
                        .append(request.getRequestURI())
                        .append(",REST METHOD ")
                        .append(joinPoint.getSignature().getName())
                        .append("()")
                        .append(" args:");

                Object[] args = joinPoint.getArgs();
                if (parameterNames != null && parameterNames.length != 0) {
                    for (int i = 0; i < parameterNames.length; ++i) {
                        // 跳过 request/response/文件对象，避免日志体积过大或序列化失败。
                        if (!(args[i] instanceof HttpServletResponse) && !(args[i] instanceof MultipartFile) && !(args[i] instanceof HttpServletRequest)) {
                            // 先检查是否为大对象，避免序列化浪费内存
                            String largeObjectDesc = checkLargeObject(args[i]);
                            if (largeObjectDesc != null) {
                                logMsg.append(parameterNames[i]).append(":").append(largeObjectDesc).append(",");
                            } else {
                                // 不是大对象，进行序列化并裁剪
                                String jsonStr = MyJson.toJson(args[i]);
                                logMsg.append(parameterNames[i]).append(":").append(truncateJson(jsonStr)).append(",");
                            }
                        }
                    }
                } else {
                    logMsg.append("null ");
                }

                logger.info(logMsg.toString());
                startTime = System.currentTimeMillis();
            } catch (Exception e) {
                logger.error("print REST log error", e);
                return joinPoint.proceed();
            }

            Object object = joinPoint.proceed();

            try {
                // 判断object是否支持toJson
                if(object.getClass() == MyResult.class) {
                    logMsg = new StringBuilder();
                    logMsg.append("Exit REST method ")
                            .append(joinPoint.getSignature().getName())
                            .append("()")
                            .append(" result:{},")
                            .append("use time:{}");

                    // 先检查返回结果是否为大对象
                    String largeObjectDesc = checkLargeObject(object);
                    if (largeObjectDesc != null) {
                        logger.info(logMsg.toString(), largeObjectDesc, System.currentTimeMillis() - startTime);
                    } else {
                        // 不是大对象，进行序列化并裁剪
                        String resultJson = MyJson.toJson(object);
                        logger.info(logMsg.toString(), truncateJson(resultJson), System.currentTimeMillis() - startTime);
                    }
                }
            } catch (Exception e) {
                logger.error("print REST log error", e);
            }
            return object;
        }
    }
}
