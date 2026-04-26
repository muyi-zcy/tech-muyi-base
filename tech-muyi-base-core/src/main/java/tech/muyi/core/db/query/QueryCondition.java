package tech.muyi.core.db.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: QueryCondition
 *
 * <p>说明：用于承载前端传入的“可组合条件树”。单个节点表达：
 * - 字段（column）
 * - 操作符（operator，AND/OR/EQ/IN/...）
 * - 值（value）
 * - 子条件（children，用于 AND/OR 嵌套）
 *
 * <p>注意：column 为前端字段名（通常是驼峰）；数据库列名通过 {@link #getDbColumn()} 转为下划线。</p>
 *
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
        // 对外统一使用数据库列名（下划线），避免 QueryWrapper 直接拼接驼峰导致列不存在。
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
                // 大写字母前加下划线，并转为小写（ASCII +32）。
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}
