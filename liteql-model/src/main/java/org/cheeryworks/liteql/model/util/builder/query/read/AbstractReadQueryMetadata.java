package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.builder.query.read.join.ReadQueryJoinMetadata;

public abstract class AbstractReadQueryMetadata {

    private TypeName domainTypeName;

    private FieldDefinitions fields = new FieldDefinitions();

    private QueryConditions conditions = new QueryConditions();

    private ReadQueryJoinMetadata[] liteQLReadQueryJoins;

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public FieldDefinitions getFields() {
        return fields;
    }

    public void setFields(FieldDefinitions fields) {
        this.fields = fields;
    }

    public QueryConditions getConditions() {
        return conditions;
    }

    public void setConditions(QueryConditions conditions) {
        this.conditions = conditions;
    }

    public ReadQueryJoinMetadata[] getLiteQLReadQueryJoins() {
        return liteQLReadQueryJoins;
    }

    public void setLiteQLReadQueryJoins(ReadQueryJoinMetadata[] liteQLReadQueryJoins) {
        this.liteQLReadQueryJoins = liteQLReadQueryJoins;
    }

}
