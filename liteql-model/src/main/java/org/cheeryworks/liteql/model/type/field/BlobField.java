package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;

public class BlobField extends AbstractNullableField {

    public BlobField() {
        super(DataType.Blob);
    }

}
