package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.type.field.Field;

import java.util.Map;

public interface SqlReadQuery<T> extends SqlQuery<T> {

    String getTotalSql();

    void setTotalSql(String totalSql);

    T getTotalSqlParameters();

    void setTotalSqlParameters(T totalSqlParameters);

    Map<String, Field> getFields();

    void setFields(Map<String, Field> fields);

}
