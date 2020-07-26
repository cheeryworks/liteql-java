package org.cheeryworks.liteql.sql;

public interface SqlQuery {

    String getSql();

    void setSql(String sql);

    Object[] getSqlParameters();

    void setSqlParameters(Object[] sqlParameters);

}
