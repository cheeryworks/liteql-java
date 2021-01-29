package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.ClobField;

public class DefaultClobField extends AbstractNullableField implements ClobField {

    public DefaultClobField() {
        super();
    }

    public DefaultClobField(Boolean graphQLField) {
        super(graphQLField);
    }

}
