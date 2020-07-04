package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.UserEntity;

import java.util.Map;

public interface AuditingService {

    void auditingDomainObject(Map<String, Object> domainObject, DomainType domainType, UserEntity user);

    void auditingExistedDomainObject(Map<String, Object> domainObject, DomainType domainType, UserEntity user);

}
