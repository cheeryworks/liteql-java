package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.save.SaveQuery;
import org.cheeryworks.liteql.model.type.DomainType;

public interface SqlQueryParser {

    SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery);

    SqlSaveQuery getSqlSaveQuery(SaveQuery saveQuery, DomainType domainType);

    SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery);

}
