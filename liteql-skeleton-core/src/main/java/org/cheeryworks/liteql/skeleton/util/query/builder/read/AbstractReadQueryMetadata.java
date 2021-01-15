package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.QueryConditions;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.join.ReadQueryJoinMetadata;

public abstract class AbstractReadQueryMetadata {

    private TypeName domainTypeName;

    private FieldDefinitions fields = new FieldDefinitions();

    private QueryConditions conditions = new QueryConditions();

    private ReadQueryJoinMetadata[] readQueryJoinMetadataArray;

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

    public ReadQueryJoinMetadata[] getReadQueryJoinMetadataArray() {
        return readQueryJoinMetadataArray;
    }

    public void setReadQueryJoinMetadataArray(ReadQueryJoinMetadata[] readQueryJoinMetadataArray) {
        this.readQueryJoinMetadataArray = readQueryJoinMetadataArray;
    }

}
