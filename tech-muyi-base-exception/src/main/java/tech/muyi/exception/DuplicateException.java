package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

/**
 * 数据重复异常
 *
 * <p>用于数据重复、唯一性约束冲突的场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class DuplicateException extends BizException {

    public DuplicateException(String entityName, Object value) {
        super(CommonErrorCodeEnum.DATA_ALREADY_EXISTED,
                String.format("%s[%s]已存在", entityName, value));
    }

    public DuplicateException(String message) {
        super(CommonErrorCodeEnum.DATA_ALREADY_EXISTED, message);
    }
}
