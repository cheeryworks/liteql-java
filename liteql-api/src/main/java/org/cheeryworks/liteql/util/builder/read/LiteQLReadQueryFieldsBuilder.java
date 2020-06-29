package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.util.builder.read.join.LiteQLReadQueryJoinsBuilder;

public class LiteQLReadQueryFieldsBuilder {

    private LiteQLReadQuery liteQLReadQuery;

    public LiteQLReadQueryFieldsBuilder(LiteQLReadQuery liteQLReadQuery) {
        this.liteQLReadQuery = liteQLReadQuery;
    }

    public LiteQLReadQueryJoinsBuilder fields(LiteQLReadQueryField... liteQLReadQueryFields) {
        for (LiteQLReadQueryField liteQLReadQueryField : liteQLReadQueryFields) {
            this.liteQLReadQuery.getFields().add(
                    new FieldDefinition(liteQLReadQueryField.getName(), liteQLReadQueryField.getAlias()));
        }

        return new LiteQLReadQueryJoinsBuilder(this.liteQLReadQuery);
    }

}
