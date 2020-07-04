package org.cheeryworks.liteql.model.type;

import java.util.Date;

public interface AuditEntity extends DomainInterface {

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

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isDeleted();

    void setDeleted(boolean deleted);

    boolean isDeletable();

    void setDeletable(boolean deletable);

    boolean isInherent();

    void setInherent(boolean inherent);

    String getCreatorId();

    void setCreatorId(String creatorId);

    String getCreatorName();

    void setCreatorName(String creatorName);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    String getLastModifierId();

    void setLastModifierId(String lastModifierId);

    String getLastModifierName();

    void setLastModifierName(String lastModifierName);

    Date getLastModifiedTime();

    void setLastModifiedTime(Date lastModifiedTime);

}
