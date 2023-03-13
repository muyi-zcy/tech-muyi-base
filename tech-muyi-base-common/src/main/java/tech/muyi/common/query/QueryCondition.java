package tech.muyi.common.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: QueryCondition
 * date: 2022/1/9 17:54
 * author: muyi
 * version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCondition {
    private String column;
    @Builder.Default
    private String operator = "and";
    private Object value;
    private List<QueryCondition> children;

    public String getDbColumn() {
        return toUnderlineCase(this.column);
    }

    private static String toUnderlineCase(String camelCaseStr) {
        if (camelCaseStr == null) {
            return null;
        }
        // 将驼峰字符串转换成数组
        char[] charArray = camelCaseStr.toCharArray();
        StringBuffer buffer = new StringBuffer();
        //处理字符串
        for (int i = 0, l = charArray.length; i < l; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}
