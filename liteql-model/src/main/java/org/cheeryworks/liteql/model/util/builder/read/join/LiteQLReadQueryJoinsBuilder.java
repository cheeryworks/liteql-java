package org.cheeryworks.liteql.model.util.builder.read.join;

import org.cheeryworks.liteql.model.util.builder.read.LiteQLReadQuery;
import org.cheeryworks.liteql.model.util.builder.read.LiteQLReadQueryConditionsBuilder;

public class LiteQLReadQueryJoinsBuilder extends LiteQLReadQueryConditionsBuilder {

    private LiteQLReadQuery liteQLReadQuery;

    public LiteQLReadQueryJoinsBuilder(LiteQLReadQuery liteQLReadQuery) {
        super(liteQLReadQuery);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public LiteQLReadQueryConditionsBuilder joins(LiteQLReadQueryJoin... liteQLReadQueryJoins) {
        this.liteQLReadQuery.setLiteQLReadQueryJoins(liteQLReadQueryJoins);

        return new LiteQLReadQueryConditionsBuilder(this.liteQLReadQuery);
    }

}
