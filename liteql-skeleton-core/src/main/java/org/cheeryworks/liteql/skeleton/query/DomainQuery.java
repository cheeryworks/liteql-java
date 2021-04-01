package org.cheeryworks.liteql.skeleton.query;

import org.cheeryworks.liteql.skeleton.schema.TypeName;

public interface DomainQuery extends Query {

    TypeName getDomainTypeName();

    QueryConditions getAccessDecisionConditions();

}
