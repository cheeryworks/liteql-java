package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.service.sql.SqlParser;
import org.cheeryworks.liteql.sql.SqlDeleteQuery;
import org.cheeryworks.liteql.sql.SqlReadQuery;
import org.cheeryworks.liteql.sql.SqlSaveQuery;

public interface SqlQueryParser extends SqlParser {

    SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery);

    SqlSaveQuery getSqlSaveQuery(AbstractSaveQuery saveQuery, DomainType domainType);

    SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery);

}