package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.AuditType;
import org.cheeryworks.liteql.model.UserType;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.util.LiteQL;

import java.sql.Timestamp;
import java.util.Map;

public class DefaultQueryAuditingService implements QueryAuditingService {

    @Override
    public void auditingDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, UserType user) {
        if (domainTypeDefinition.implement(LiteQL.SchemaUtils.getTypeName(AuditType.class))) {
            if (domainObject.get(AuditType.INHERENT_FIELD_NAME) == null) {
                domainObject.put(AuditType.INHERENT_FIELD_NAME, false);
            }

            if (domainObject.get(AuditType.ENABLED_FIELD_NAME) == null) {
                domainObject.put(AuditType.ENABLED_FIELD_NAME, true);
            }

            if (domainObject.get(AuditType.DELETABLE_FIELD_NAME) == null) {
                domainObject.put(AuditType.DELETABLE_FIELD_NAME, true);
            }

            if (domainObject.get(AuditType.DELETED_FIELD_NAME) == null) {
                domainObject.put(AuditType.DELETED_FIELD_NAME, false);
            }

            Timestamp currentDate = new Timestamp(System.currentTimeMillis());

            if (domainObject.get(AuditType.CREATE_TIME_FIELD_NAME) == null) {
                domainObject.put(AuditType.CREATE_TIME_FIELD_NAME, currentDate);
            }

            domainObject.put(AuditType.LAST_MODIFIED_TIME_FIELD_NAME, currentDate);

            if (user != null) {
                if (domainObject.get(AuditType.CREATOR_ID_FIELD_NAME) == null) {
                    domainObject.put(AuditType.CREATOR_ID_FIELD_NAME, user.getId());
                }

                domainObject.put(AuditType.LAST_MODIFIER_ID_FIELD_NAME, user.getId());
            }
        }
    }

    @Override
    public void auditingExistedDomainObject(
            Map<String, Object> domainObject, DomainTypeDefinition domainTypeDefinition, UserType user) {
        if (domainTypeDefinition.implement(LiteQL.SchemaUtils.getTypeName(AuditType.class))) {
            Timestamp currentDate = new Timestamp(System.currentTimeMillis());

            domainObject.put(AuditType.LAST_MODIFIED_TIME_FIELD_NAME, currentDate);

            if (user != null) {
                domainObject.put(AuditType.LAST_MODIFIER_ID_FIELD_NAME, user.getId());
            }
        }
    }

}
