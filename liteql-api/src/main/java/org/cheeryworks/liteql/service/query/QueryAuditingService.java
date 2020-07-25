package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.UserEntity;

import java.util.Map;

public interface QueryAuditingService {

    void auditingDomainObject(Map<String, Object> domainObject, DomainType domainType, UserEntity user);

    void auditingExistedDomainObject(Map<String, Object> domainObject, DomainType domainType, UserEntity user);

}
