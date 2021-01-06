package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;

import java.util.Map;

public interface QueryAuditingService {

    void auditingDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, QueryContext queryContext);

    void auditingExistedDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, QueryContext queryContext);

}
