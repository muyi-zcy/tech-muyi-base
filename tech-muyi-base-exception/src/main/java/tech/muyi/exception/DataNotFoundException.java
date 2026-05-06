package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 数据不存在异常
 *
 * <p>用于查询数据不存在的场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class DataNotFoundException extends BizException {

    public DataNotFoundException(String entityName, Object id) {
        super(CommonErrorCodeEnum.RESULE_DATA_NONE,
                String.format("%s[%s]不存在", entityName, id));
    }

    public DataNotFoundException(String message) {
        super(CommonErrorCodeEnum.RESULE_DATA_NONE, message);
    }
}
