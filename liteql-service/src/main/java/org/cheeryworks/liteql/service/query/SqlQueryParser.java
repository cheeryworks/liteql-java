package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.type.DomainType;

public interface SqlQueryParser {

    SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery);

    SqlSaveQuery getSqlSaveQuery(AbstractSaveQuery saveQuery, DomainType domainType);

    SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery);

}
