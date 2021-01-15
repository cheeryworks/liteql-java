package org.cheeryworks.liteql.skeleton.service.query;

import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;

import java.util.Map;

public interface QueryAuditingService {

    void auditingDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, QueryContext queryContext);

    void auditingExistedDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, QueryContext queryContext);

}
