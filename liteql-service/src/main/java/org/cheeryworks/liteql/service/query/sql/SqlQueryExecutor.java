package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.query.read.result.ReadResults;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.service.sql.SqlExecutor;

import java.util.Map;

public interface SqlQueryExecutor extends SqlExecutor {

    ReadResults read(TypeName domainTypeName, String sql, Map<String, Field> fields, Object[] parameters);

}
