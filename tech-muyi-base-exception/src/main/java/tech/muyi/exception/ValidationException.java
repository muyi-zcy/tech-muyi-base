package tech.muyi.exception;

import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数校验异常
 *
 * <p>用于参数校验失败场景</p>
 *
 * @author: muyi
 * @date: 2026/05/05
 */
public class ValidationException extends BizException {

    private List<FieldError> fieldErrors;

    public ValidationException(String message) {
        super(CommonErrorCodeEnum.PARAM_IS_INVALID, message);
    }

    public ValidationException(List<FieldError> fieldErrors) {
        super(CommonErrorCodeEnum.PARAM_IS_INVALID, buildMessage(fieldErrors));
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    private static String buildMessage(List<FieldError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "参数校验失败";
        }
        return errors.stream()
                .map(e -> e.getField() + ":" + e.getMessage())
                .collect(Collectors.joining(", "));
    }

    /**
     * 字段错误信息
     */
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String message;

        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public String getMessage() {
            return message;
        }
    }
}
