package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.type.AuditEntity;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.UserEntity;

import java.sql.Timestamp;
import java.util.Map;

public class DefaultAuditingService implements AuditingService {

    @Override
    public void auditingDomainObject(
            Map<String, Object> domainObject, DomainType domainType, UserEntity user) {
        if (domainType.implement(AuditEntity.class.getSimpleName())) {
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
                    domainObject.put(AuditEntity.CREATOR_NAME_FIELD_NAME, user.getName());
                }

                domainObject.put(AuditEntity.LAST_MODIFIER_ID_FIELD_NAME, user.getId());
                domainObject.put(AuditEntity.LAST_MODIFIER_NAME_FIELD_NAME, user.getName());
            }
        }
    }

    @Override
    public void auditingExistedDomainObject(
            Map<String, Object> domainObject, DomainType domainType, UserEntity user) {
        if (domainType.implement(AuditEntity.class.getSimpleName())) {
            Timestamp currentDate = new Timestamp(System.currentTimeMillis());

            domainObject.put(AuditEntity.LAST_MODIFIED_TIME_FIELD_NAME, currentDate);

            if (user != null) {
                domainObject.put(AuditEntity.LAST_MODIFIER_ID_FIELD_NAME, user.getId());
                domainObject.put(AuditEntity.LAST_MODIFIER_NAME_FIELD_NAME, user.getName());
            }
        }
    }

}
