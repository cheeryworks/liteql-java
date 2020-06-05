package org.cheeryworks.liteql.model.query.read;

import org.cheeryworks.liteql.model.query.AbstractConditionalQuery;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinitions;

public abstract class AbstractFieldReadQuery extends AbstractConditionalQuery {

    private FieldDefinitions fields = new FieldDefinitions();

    public FieldDefinitions getFields() {
        return fields;
    }

    public void setFields(FieldDefinitions fields) {
        this.fields = fields;
    }

}
