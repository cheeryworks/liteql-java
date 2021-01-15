package org.cheeryworks.liteql.skeleton.service.sql;

import java.util.List;

public interface SqlExecutor {

    void isDatabaseReady();

    long count(String sql, Object[] parameters);

    int execute(String sql, Object[] parameters);

    void executeBatch(String sql, List<Object[]> parametersList);

}
