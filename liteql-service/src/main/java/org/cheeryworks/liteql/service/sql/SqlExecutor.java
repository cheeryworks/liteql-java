package org.cheeryworks.liteql.service.sql;

import java.util.List;
import java.util.Map;

public interface SqlExecutor {

    void isDatabaseReady();

    long count(String sql, Object[] parameters);

    int execute(String sql, Object[] parameters);

    void executeBatch(String sql, List<Object[]> parametersList);

    void executeNamedBatch(String sql, List<Map<String, Object>> parametersList);

}
