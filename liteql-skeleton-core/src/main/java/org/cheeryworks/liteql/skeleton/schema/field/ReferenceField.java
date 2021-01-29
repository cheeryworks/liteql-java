package org.cheeryworks.liteql.skeleton.schema.field;

import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

public interface ReferenceField extends NullableField {

    TypeName getDomainTypeName();

    TypeName getMappedDomainTypeName();

    boolean isCollection();

    @Override
    default DataType getType() {
        return DataType.Reference;
    }

}
