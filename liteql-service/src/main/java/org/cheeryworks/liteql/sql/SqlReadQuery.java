package org.cheeryworks.liteql.sql;

import java.util.Map;

public interface SqlReadQuery extends SqlQuery {

    String getTotalSql();

    void setTotalSql(String totalSql);

    Object[] getTotalSqlParameters();

    void setTotalSqlParameters(Object[] totalSqlParameters);

    Map<String, String> getFields();

    void setFields(Map<String, String> fields);

}
