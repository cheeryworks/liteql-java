package org.cheeryworks.liteql.skeleton.service.query.sql;

import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.service.sql.SqlExecutor;
import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;

public interface SqlQueryExecutor extends SqlExecutor {

    ReadResults read(SqlReadQuery sqlReadQuery);

}
