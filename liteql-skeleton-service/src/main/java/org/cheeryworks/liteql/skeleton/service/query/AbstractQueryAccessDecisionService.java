package org.cheeryworks.liteql.skeleton.service.query;

import org.cheeryworks.liteql.skeleton.query.QueryConditions;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;

public abstract class AbstractQueryAccessDecisionService implements QueryAccessDecisionService {

    protected boolean decideToCreate(AbstractSaveQuery saveQuery, QueryConditions accessDecisionConditions) {
        return true;
    }

}
