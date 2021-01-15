package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.TypeName;

public interface ReferenceField extends NullableField {
    TypeName getDomainTypeName();

    TypeName getMappedDomainTypeName();

    boolean isCollection();
}
