package org.cheeryworks.liteql.model.query.read.join;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class JoinedQueryCondition extends QueryCondition {

    private Class fieldType;

    private Class valueType;

    public Class getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }

    public Class getValueType() {
        return valueType;
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }

}