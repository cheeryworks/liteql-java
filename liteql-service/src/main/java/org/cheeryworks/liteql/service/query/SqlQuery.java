package org.cheeryworks.liteql.service.query;

public interface SqlQuery<T> {

    String getSql();

    void setSql(String sql);

    T getSqlParameters();

    void setSqlParameters(T sqlParameters);

}
