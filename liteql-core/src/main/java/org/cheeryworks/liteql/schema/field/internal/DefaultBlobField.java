package org.cheeryworks.liteql.schema.field.internal;

import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.BlobField;

public class DefaultBlobField extends AbstractNullableField implements BlobField {

    public DefaultBlobField() {
        this(null);
    }

    public DefaultBlobField(Boolean graphQLField) {
        super(DataType.Blob, graphQLField);
    }

}
