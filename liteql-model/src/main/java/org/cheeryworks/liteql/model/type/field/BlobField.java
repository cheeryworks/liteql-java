package org.cheeryworks.liteql.model.type.field;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.AbstractDomainTypeField;

public class BlobField extends AbstractDomainTypeField {

    public BlobField() {
        this.setType(DataType.Blob.name().toLowerCase());
    }

}
