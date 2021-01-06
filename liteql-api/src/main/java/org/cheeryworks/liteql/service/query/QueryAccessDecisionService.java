package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.UserType;
import org.cheeryworks.liteql.query.DomainQuery;

public interface QueryAccessDecisionService {

    void decide(UserType user, DomainQuery query);

}
