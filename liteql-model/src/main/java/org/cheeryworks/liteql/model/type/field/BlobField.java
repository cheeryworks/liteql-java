package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractNullableDomainTypeField;

public class BlobField extends AbstractNullableDomainTypeField {

    public BlobField() {
        this.setType(DataType.Blob.name().toLowerCase());
    }

}
