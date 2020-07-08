package org.cheeryworks.liteql.model.util.builder;

import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.builder.delete.LiteQLDeleteQuery;
import org.cheeryworks.liteql.model.util.builder.read.LiteQLReadQueryFieldsBuilder;
import org.cheeryworks.liteql.model.util.builder.save.LiteQLSaveQueriesBuilder;
import org.cheeryworks.liteql.model.util.builder.delete.LiteQLDeleteQueriesBuilder;
import org.cheeryworks.liteql.model.util.builder.delete.LiteQLDeleteQueryConditionsBuilder;
import org.cheeryworks.liteql.model.util.builder.read.LiteQLReadQuery;
import org.cheeryworks.liteql.model.util.builder.save.LiteQLSaveQuery;
import org.cheeryworks.liteql.model.util.builder.save.LiteQLSaveQueryFieldsBuilder;

public class LiteQLBuilder {

    public static LiteQLReadQueryFieldsBuilder read(TypeName domainType) {
        return LiteQLReadQuery.read(domainType);
    }

    public static LiteQLSaveQueryFieldsBuilder<CreateQuery> create(TypeName domainType) {
        return LiteQLSaveQuery.create(domainType);
    }

    public static LiteQLSaveQueryFieldsBuilder<UpdateQuery> update(TypeName domainType) {
        return LiteQLSaveQuery.update(domainType);
    }

    public static LiteQLSaveQueriesBuilder save(LiteQLSaveQuery... liteQLSaveQueries) {
        return new LiteQLSaveQueriesBuilder(liteQLSaveQueries);
    }

    public static LiteQLDeleteQueryConditionsBuilder delete(TypeName domainTypeName) {
        return LiteQLDeleteQuery.delete(domainTypeName);
    }

    public static LiteQLDeleteQueriesBuilder delete(LiteQLDeleteQuery... liteQLDeleteQueries) {
        return new LiteQLDeleteQueriesBuilder(liteQLDeleteQueries);
    }

}
