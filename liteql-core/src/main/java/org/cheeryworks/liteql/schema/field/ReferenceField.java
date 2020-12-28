package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.TypeName;

public interface ReferenceField extends NullableField {
    TypeName getDomainTypeName();

    TypeName getMappedDomainTypeName();

    boolean isCollection();
}
