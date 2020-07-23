package org.cheeryworks.liteql.service.auditing;

import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.UserEntity;

import java.util.Map;

public interface AuditingService {

    void auditingDomainObject(Map<String, Object> domainObject, DomainType domainType, UserEntity user);

    void auditingExistedDomainObject(Map<String, Object> domainObject, DomainType domainType, UserEntity user);

}
