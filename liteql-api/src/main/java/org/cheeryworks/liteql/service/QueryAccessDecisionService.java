package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.query.AbstractDomainQuery;
import org.cheeryworks.liteql.model.type.UserEntity;

public interface QueryAccessDecisionService {

    default void decide(UserEntity user, AbstractDomainQuery query) {
        
    }

}
