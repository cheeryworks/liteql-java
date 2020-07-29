package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.AbstractDomainQuery;
import org.cheeryworks.liteql.model.UserEntity;

public interface QueryAccessDecisionService {

    default void decide(UserEntity user, AbstractDomainQuery query) {

    }

}
