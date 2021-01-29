package org.cheeryworks.liteql.skeleton.schema.field.internal;

import org.cheeryworks.liteql.skeleton.schema.field.BooleanField;

public class DefaultBooleanField extends AbstractField implements BooleanField {

    public DefaultBooleanField() {
        super();
    }

    public DefaultBooleanField(Boolean graphQLField) {
        super(graphQLField);
    }

}
