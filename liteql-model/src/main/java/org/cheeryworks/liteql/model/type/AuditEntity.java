package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.annotation.Position;
import org.cheeryworks.liteql.model.annotation.ReferenceField;
import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.annotation.graphql.GraphQLField;
import org.cheeryworks.liteql.model.util.LiteQLConstants;

import java.util.Date;

@ResourceDefinition(namespace = LiteQLConstants.NAMESPACE)
public interface AuditEntity extends Entity {

    String ENABLED_FIELD_NAME = "enabled";

    String DELETED_FIELD_NAME = "deleted";

    String DELETABLE_FIELD_NAME = "deletable";

    String INHERENT_FIELD_NAME = "inherent";

    String CREATOR_ID_FIELD_NAME = "creatorId";

    String CREATOR_NAME_FIELD_NAME = "creatorName";

    String CREATE_TIME_FIELD_NAME = "createTime";

    String LAST_MODIFIER_ID_FIELD_NAME = "lastModifierId";

    String LAST_MODIFIER_NAME_FIELD_NAME = "lastModifierName";

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
    @GraphQLField(name = "creator")
    @ReferenceField(targetDomainType = UserEntity.class)
    String getCreatorId();

    @Position(6)
    @GraphQLField(ignore = true)
    String getCreatorName();

    @Position(7)
    Date getCreateTime();

    @Position(8)
    @GraphQLField(name = "lastModifier")
    @ReferenceField(targetDomainType = UserEntity.class)
    String getLastModifierId();

    @Position(9)
    @GraphQLField(ignore = true)
    String getLastModifierName();

    @Position(10)
    Date getLastModifiedTime();

}
