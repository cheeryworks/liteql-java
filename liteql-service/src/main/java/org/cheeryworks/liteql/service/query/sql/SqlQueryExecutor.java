package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.query.read.result.ReadResults;
import org.cheeryworks.liteql.service.sql.SqlExecutor;
import org.cheeryworks.liteql.sql.SqlReadQuery;

public interface SqlQueryExecutor extends SqlExecutor {

    ReadResults read(SqlReadQuery sqlReadQuery);

}
