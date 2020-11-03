package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.annotation.LiteQLFieldPosition;
import org.cheeryworks.liteql.schema.annotation.LiteQLReferenceField;
import org.cheeryworks.liteql.schema.annotation.LiteQLType;
import org.cheeryworks.liteql.util.LiteQL;

import java.util.Date;

@LiteQLType(schema = LiteQL.Constants.SCHEMA, version = LiteQL.Constants.SPECIFICATION_VERSION)
public interface AuditType extends DomainType {

    String ENABLED_FIELD_NAME = "enabled";

    String DELETED_FIELD_NAME = "deleted";

    String DELETABLE_FIELD_NAME = "deletable";

    String INHERENT_FIELD_NAME = "inherent";

    String CREATOR_REFERENCE_FIELD_NAME = "creator";

    String CREATOR_ID_FIELD_NAME = "creatorId";

    String CREATE_TIME_FIELD_NAME = "createTime";

    String LAST_MODIFIER_ID_FIELD_NAME = "lastModifierId";

    String LAST_MODIFIED_TIME_FIELD_NAME = "lastModifiedTime";

    @LiteQLFieldPosition(1)
    boolean isEnabled();

    @LiteQLFieldPosition(2)
    boolean isDeleted();

    @LiteQLFieldPosition(3)
    boolean isDeletable();

    @LiteQLFieldPosition(4)
    boolean isInherent();

    @LiteQLFieldPosition(5)
    @LiteQLReferenceField(name = "creator", targetDomainType = UserType.class)
    String getCreatorId();

    @LiteQLFieldPosition(6)
    Date getCreateTime();

    @LiteQLFieldPosition(7)
    @LiteQLReferenceField(name = "lastModifier", targetDomainType = UserType.class)
    String getLastModifierId();

    @LiteQLFieldPosition(8)
    Date getLastModifiedTime();

}
