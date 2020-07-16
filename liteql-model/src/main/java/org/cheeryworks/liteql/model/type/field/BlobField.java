package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class BlobField extends AbstractNullableField {

    public BlobField() {
        this(null);
    }

    public BlobField(Boolean graphQLField) {
        super(DataType.Blob, graphQLField);
    }

}
