package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.schema.AuditEntity;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Trait;
import org.cheeryworks.liteql.schema.UserEntity;

import java.sql.Timestamp;
import java.util.Map;

public class DefaultQueryAuditingService implements QueryAuditingService {

    @Override
    public void auditingDomainObject(
            Map<String, Object> domainObject, DomainType domainType, UserEntity user) {
        if (domainType.implement(Trait.getTypeName(AuditEntity.class))) {
            if (domainObject.get(AuditEntity.INHERENT_FIELD_NAME) == null) {
                domainObject.put(AuditEntity.INHERENT_FIELD_NAME, false);
            }

            if (domainObject.get(AuditEntity.ENABLED_FIELD_NAME) == null) {
                domainObject.put(AuditEntity.ENABLED_FIELD_NAME, true);
            }

            if (domainObject.get(AuditEntity.DELETABLE_FIELD_NAME) == null) {
                domainObject.put(AuditEntity.DELETABLE_FIELD_NAME, true);
            }

            if (domainObject.get(AuditEntity.DELETED_FIELD_NAME) == null) {
                domainObject.put(AuditEntity.DELETED_FIELD_NAME, false);
            }

            Timestamp currentDate = new Timestamp(System.currentTimeMillis());

            if (domainObject.get(AuditEntity.CREATE_TIME_FIELD_NAME) == null) {
                domainObject.put(AuditEntity.CREATE_TIME_FIELD_NAME, currentDate);
            }

            domainObject.put(AuditEntity.LAST_MODIFIED_TIME_FIELD_NAME, currentDate);

            if (user != null) {
                if (domainObject.get(AuditEntity.CREATOR_ID_FIELD_NAME) == null) {
                    domainObject.put(AuditEntity.CREATOR_ID_FIELD_NAME, user.getId());
                }

                domainObject.put(AuditEntity.LAST_MODIFIER_ID_FIELD_NAME, user.getId());
            }
        }
    }

    @Override
    public void auditingExistedDomainObject(
            Map<String, Object> domainObject, DomainType domainType, UserEntity user) {
        if (domainType.implement(Trait.getTypeName(AuditEntity.class))) {
            Timestamp currentDate = new Timestamp(System.currentTimeMillis());

            domainObject.put(AuditEntity.LAST_MODIFIED_TIME_FIELD_NAME, currentDate);

            if (user != null) {
                domainObject.put(AuditEntity.LAST_MODIFIER_ID_FIELD_NAME, user.getId());
            }
        }
    }

}
