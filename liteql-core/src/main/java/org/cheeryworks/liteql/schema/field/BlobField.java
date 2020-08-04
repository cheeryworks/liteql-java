package org.cheeryworks.liteql.schema.field;

import org.cheeryworks.liteql.schema.enums.DataType;

public class BlobField extends AbstractNullableField {

    public BlobField() {
        this(null);
    }

    public BlobField(Boolean graphQLField) {
        super(DataType.Blob, graphQLField);
    }

}
