package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.query.ReadQuery;
import org.cheeryworks.liteql.model.query.DeleteQuery;
import org.cheeryworks.liteql.model.query.SaveQuery;
import org.cheeryworks.liteql.model.type.DomainType;

public interface SqlQueryParser {

    SqlReadQuery getSqlReadQuery(ReadQuery readQuery);

    SqlSaveQuery getSqlSaveQuery(SaveQuery saveQuery, DomainType domainType);

    SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery);

}
