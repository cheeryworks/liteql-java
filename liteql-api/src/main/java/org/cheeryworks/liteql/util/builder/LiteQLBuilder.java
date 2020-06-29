package org.cheeryworks.liteql.util.builder;

import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.util.builder.delete.LiteQLDeleteQueriesBuilder;
import org.cheeryworks.liteql.util.builder.delete.LiteQLDeleteQuery;
import org.cheeryworks.liteql.util.builder.delete.LiteQLDeleteQueryConditionsBuilder;
import org.cheeryworks.liteql.util.builder.read.LiteQLReadQuery;
import org.cheeryworks.liteql.util.builder.read.LiteQLReadQueryFieldsBuilder;
import org.cheeryworks.liteql.util.builder.save.LiteQLSaveQueriesBuilder;
import org.cheeryworks.liteql.util.builder.save.LiteQLSaveQuery;
import org.cheeryworks.liteql.util.builder.save.LiteQLSaveQueryFieldsBuilder;

public class LiteQLBuilder {

    public static LiteQLReadQueryFieldsBuilder read(DomainType domainType) {
        return LiteQLReadQuery.read(domainType);
    }

    public static LiteQLSaveQueryFieldsBuilder<CreateQuery> create(DomainType domainType) {
        return LiteQLSaveQuery.create(domainType);
    }

    public static LiteQLSaveQueryFieldsBuilder<UpdateQuery> update(DomainType domainType) {
        return LiteQLSaveQuery.update(domainType);
    }

    public static LiteQLSaveQueriesBuilder save(LiteQLSaveQuery... liteQLSaveQueries) {
        return new LiteQLSaveQueriesBuilder(liteQLSaveQueries);
    }

    public static LiteQLDeleteQueryConditionsBuilder delete(DomainType domainType) {
        return LiteQLDeleteQuery.delete(domainType);
    }

    public static LiteQLDeleteQueriesBuilder delete(LiteQLDeleteQuery... liteQLDeleteQueries) {
        return new LiteQLDeleteQueriesBuilder(liteQLDeleteQueries);
    }

}
