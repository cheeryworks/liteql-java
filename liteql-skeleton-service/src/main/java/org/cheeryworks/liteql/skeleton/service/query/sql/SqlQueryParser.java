package org.cheeryworks.liteql.skeleton.service.query.sql;

import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.service.sql.SqlParser;
import org.cheeryworks.liteql.skeleton.sql.SqlDeleteQuery;
import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;
import org.cheeryworks.liteql.skeleton.sql.SqlSaveQuery;

public interface SqlQueryParser extends SqlParser {

    SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery);

    SqlSaveQuery getSqlSaveQuery(AbstractSaveQuery saveQuery, DomainTypeDefinition domainTypeDefinition);

    SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery);

}
