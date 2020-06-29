package org.cheeryworks.liteql.util.builder.read.join;

import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.util.builder.read.LiteQLReadQueryField;

public class LiteQLReadQueryJoinFieldsBuilder {

    private LiteQLReadQueryJoin liteQLReadQueryJoin;

    public LiteQLReadQueryJoinFieldsBuilder(LiteQLReadQueryJoin liteQLReadQueryJoin) {
        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public LiteQLReadQueryJoinConditionsBuilder fields(LiteQLReadQueryField... liteQLReadQueryFields) {
        for (LiteQLReadQueryField liteQLReadQueryField : liteQLReadQueryFields) {
            this.liteQLReadQueryJoin.getFields().add(
                    new FieldDefinition(liteQLReadQueryField.getName(), liteQLReadQueryField.getAlias()));
        }

        return new LiteQLReadQueryJoinConditionsBuilder(this.liteQLReadQueryJoin);
    }

}
