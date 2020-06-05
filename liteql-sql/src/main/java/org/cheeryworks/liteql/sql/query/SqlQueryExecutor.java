package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.type.field.Field;

import java.util.List;
import java.util.Map;

public interface SqlQueryExecutor {

    long count(String sql, Object[] parameters);

    ReadResults read(String sql, Map<String, Field> fields, Object[] parameters);

    int execute(String sql, Object[] parameters);

    void executeBatch(String sql, List<Object[]> parametersList);

    void executeNamedBatch(String sql, List<Map<String, Object>> parametersList);

}
