package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.model.UserType;

import java.util.Map;

public interface QueryAuditingService {

    void auditingDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, UserType user);

    void auditingExistedDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, UserType user);

}
