package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.util.builder.read.join.LiteQLReadQueryJoin;

public abstract class AbstractLiteQLReadQuery {

    private DomainType domainType;

    private FieldDefinitions fields = new FieldDefinitions();

    private QueryConditions conditions = new QueryConditions();

    private LiteQLReadQueryJoin[] liteQLReadQueryJoins;

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domainType) {
        this.domainType = domainType;
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

    public LiteQLReadQueryJoin[] getLiteQLReadQueryJoins() {
        return liteQLReadQueryJoins;
    }

    public void setLiteQLReadQueryJoins(LiteQLReadQueryJoin[] liteQLReadQueryJoins) {
        this.liteQLReadQueryJoins = liteQLReadQueryJoins;
    }

}
