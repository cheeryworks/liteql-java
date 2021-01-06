package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.schema.TypeName;

public interface DomainQuery extends Query {
    TypeName getDomainTypeName();

    QueryConditions getAccessDecisionConditions();

    void setAccessDecisionConditions(QueryConditions queryConditions);

}
