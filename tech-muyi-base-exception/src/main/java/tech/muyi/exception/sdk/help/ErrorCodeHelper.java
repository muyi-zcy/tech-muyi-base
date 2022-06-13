package tech.muyi.exception.sdk.help;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import tech.muyi.exception.BaseErrorInfoInterface;
import tech.muyi.exception.sdk.ErrorCodoInfo;
import tech.muyi.exception.sdk.anno.ErrorCodeInfoAnno;
import tech.muyi.exception.sdk.enums.AnnoTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 获取系统内所有的状态码
 * @author: muyi
 * @date: 2022/6/12
 **/
@Service
public class ErrorCodeHelper {

    /**
     * 获取系统内所有的状态码
     * @return
     */
    public static List<ErrorCodoInfo> getAllErrorCode(){
        List<ErrorCodoInfo> errorCodoInfoList =new ArrayList<>();
        Reflections reflections = new Reflections();

        Set<Class<? extends BaseErrorInfoInterface>> enumClasses = reflections.getSubTypesOf(BaseErrorInfoInterface.class);

        enumClasses.forEach(item->{
            ErrorCodeInfoAnno errorCodeInfoAnno = item.getAnnotation(ErrorCodeInfoAnno.class);
            if(errorCodeInfoAnno == null){
                return;
            }
            BaseErrorInfoInterface[] enumConstants = item.getEnumConstants();
            List<ErrorCodoInfo> childErrorCodeInfoList = new ArrayList<>();
            for (BaseErrorInfoInterface anEnum : enumConstants) {
                childErrorCodeInfoList.add(ErrorCodoInfo.builder().parentCode(errorCodeInfoAnno.code()).typeEnum(AnnoTypeEnum.CODE).code(anEnum.getResultCode()).desc(anEnum.getResultMsg()).build());
            }
            errorCodoInfoList.add(ErrorCodoInfo.builder().parentCode(errorCodeInfoAnno.parentCode()).name(errorCodeInfoAnno.name()).typeEnum(AnnoTypeEnum.TYPE).desc(errorCodeInfoAnno.name()).code(errorCodeInfoAnno.code()).childInfo(childErrorCodeInfoList).build());
        });

        return errorCodoInfoList;
    }
}
