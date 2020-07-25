package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.save.AbstractSaveQuery;

public abstract class AbstractQueryAccessDecisionService implements QueryAccessDecisionService {

    protected boolean decideToCreate(AbstractSaveQuery saveQuery, QueryConditions accessDecisionConditions) {
        return true;
    }

}
