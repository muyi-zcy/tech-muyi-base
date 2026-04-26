package tech.muyi.core.db;

public interface DbConstant {
    /**
     * SQL 片段：限制只取最后一条/一条记录。
     *
     * <p>注意：该常量仅适用于 MySQL 方言（LIMIT），若切换数据库需评估兼容性。</p>
     */
    String SQL_LAST_LIMIT_ONE = " LIMIT 1 ";
}
