package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.type.DomainTypeField;

import java.util.Map;

public interface SqlReadQuery<T> extends SqlQuery<T> {

    String getTotalSql();

    void setTotalSql(String totalSql);

    T getTotalSqlParameters();

    void setTotalSqlParameters(T totalSqlParameters);

    Map<String, DomainTypeField> getFields();

    void setFields(Map<String, DomainTypeField> fields);

}
