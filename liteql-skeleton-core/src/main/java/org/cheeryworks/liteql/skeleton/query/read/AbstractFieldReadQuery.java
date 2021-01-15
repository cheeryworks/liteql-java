package org.cheeryworks.liteql.skeleton.query.read;

import org.cheeryworks.liteql.skeleton.query.AbstractConditionalQuery;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinitions;

public abstract class AbstractFieldReadQuery extends AbstractConditionalQuery {

    private FieldDefinitions fields;

    public FieldDefinitions getFields() {
        return fields;
    }

    public void setFields(FieldDefinitions fields) {
        this.fields = fields;
    }

}
