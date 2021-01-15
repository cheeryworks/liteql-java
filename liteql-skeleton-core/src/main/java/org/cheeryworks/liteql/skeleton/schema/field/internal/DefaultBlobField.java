package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.enums.DataType;
import org.cheeryworks.liteql.skeleton.schema.field.BlobField;

public class DefaultBlobField extends AbstractNullableField implements BlobField {

    public DefaultBlobField() {
        this(null);
    }

    public DefaultBlobField(Boolean graphQLField) {
        super(DataType.Blob, graphQLField);
    }

}
