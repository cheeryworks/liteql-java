package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.BlobField;

public class DefaultBlobField extends AbstractNullableField implements BlobField {

    public DefaultBlobField() {
        super();
    }

    public DefaultBlobField(Boolean graphQLField) {
        super(graphQLField);
    }

}
