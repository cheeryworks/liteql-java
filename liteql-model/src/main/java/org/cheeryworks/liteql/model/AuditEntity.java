package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.Position;
import org.cheeryworks.liteql.schema.annotation.ReferenceField;

import java.util.Date;

public interface AuditEntity extends Entity {

    String ENABLED_FIELD_NAME = "enabled";

    String DELETED_FIELD_NAME = "deleted";

    String DELETABLE_FIELD_NAME = "deletable";

    String INHERENT_FIELD_NAME = "inherent";

    String CREATOR_ID_FIELD_NAME = "creatorId";

    String CREATE_TIME_FIELD_NAME = "createTime";

    String LAST_MODIFIER_ID_FIELD_NAME = "lastModifierId";

    String LAST_MODIFIED_TIME_FIELD_NAME = "lastModifiedTime";

    @Position(1)
    boolean isEnabled();

    @Position(2)
    boolean isDeleted();

    @Position(3)
    boolean isDeletable();

    @Position(4)
    boolean isInherent();

    @Position(5)
    @ReferenceField(name = "creator", targetDomainType = UserEntity.class)
    String getCreatorId();

    @Position(6)
    Date getCreateTime();

    @Position(7)
    @ReferenceField(name = "lastModifier", targetDomainType = UserEntity.class)
    String getLastModifierId();

    @Position(8)
    Date getLastModifiedTime();

}
