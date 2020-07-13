package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.service.SqlExecutor;

import java.util.Map;

public interface SqlQueryExecutor extends SqlExecutor {

    ReadResults read(String sql, Map<String, Field> fields, Object[] parameters);

}
