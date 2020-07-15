package org.cheeryworks.liteql.model.util.builder.query;

import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.builder.query.delete.DeleteQueryMetadata;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryFieldsBuilder;
import org.cheeryworks.liteql.model.util.builder.query.save.SaveQueriesBuilder;
import org.cheeryworks.liteql.model.util.builder.query.delete.DeleteQueriesBuilder;
import org.cheeryworks.liteql.model.util.builder.query.delete.DeleteQueryConditionsBuilder;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryMetadata;
import org.cheeryworks.liteql.model.util.builder.query.save.SaveQueryMetadata;
import org.cheeryworks.liteql.model.util.builder.query.save.SaveQueryFieldsBuilder;

public class QueryBuilder {

    public static ReadQueryFieldsBuilder read(TypeName domainType) {
        return ReadQueryMetadata.read(domainType);
    }

    public static SaveQueryFieldsBuilder<CreateQuery> create(TypeName domainType) {
        return SaveQueryMetadata.create(domainType);
    }

    public static SaveQueryFieldsBuilder<UpdateQuery> update(TypeName domainType) {
        return SaveQueryMetadata.update(domainType);
    }

    public static SaveQueriesBuilder save(SaveQueryMetadata... liteQLSaveQueries) {
        return new SaveQueriesBuilder(liteQLSaveQueries);
    }

    public static DeleteQueryConditionsBuilder delete(TypeName domainTypeName) {
        return DeleteQueryMetadata.delete(domainTypeName);
    }

    public static DeleteQueriesBuilder delete(DeleteQueryMetadata... liteQLDeleteQueries) {
        return new DeleteQueriesBuilder(liteQLDeleteQueries);
    }

}
