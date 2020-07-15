package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryMetadata;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryConditionsBuilder;

public class ReadQueryJoinsBuilder extends ReadQueryConditionsBuilder {

    private ReadQueryMetadata liteQLReadQuery;

    public ReadQueryJoinsBuilder(ReadQueryMetadata liteQLReadQuery) {
        super(liteQLReadQuery);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public ReadQueryConditionsBuilder joins(ReadQueryJoinMetadata... liteQLReadQueryJoins) {
        this.liteQLReadQuery.setLiteQLReadQueryJoins(liteQLReadQueryJoins);

        return new ReadQueryConditionsBuilder(this.liteQLReadQuery);
    }

}
