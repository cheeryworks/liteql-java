package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.service.sql.SqlExecutor;

import java.util.Map;

public interface SqlQueryExecutor extends SqlExecutor {

    ReadResults read(TypeName domainTypeName, String sql, Map<String, Field> fields, Object[] parameters);

}
