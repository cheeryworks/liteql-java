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

    public static ReadQueryFieldsBuilder read(TypeName domainTypeName) {
        ReadQueryMetadata readQueryMetadata = new ReadQueryMetadata();

        readQueryMetadata.setDomainTypeName(domainTypeName);

        return new ReadQueryFieldsBuilder(readQueryMetadata);
    }

    public static SaveQueryFieldsBuilder<CreateQuery> create(TypeName domainTypeName) {
        SaveQueryMetadata<CreateQuery> saveQueryMetadata = new SaveQueryMetadata<>(new CreateQuery());

        saveQueryMetadata.setDomainTypeName(domainTypeName);

        return new SaveQueryFieldsBuilder<>(saveQueryMetadata);
    }

    public static SaveQueryFieldsBuilder<UpdateQuery> update(TypeName domainTypeName) {
        SaveQueryMetadata<UpdateQuery> saveQueryMetadata = new SaveQueryMetadata<>(new UpdateQuery());

        saveQueryMetadata.setDomainTypeName(domainTypeName);

        return new SaveQueryFieldsBuilder<>(saveQueryMetadata);
    }

    public static SaveQueriesBuilder save(SaveQueryMetadata... saveQueryMetadataArray) {
        return new SaveQueriesBuilder(saveQueryMetadataArray);
    }

    public static DeleteQueryConditionsBuilder delete(TypeName domainTypeName) {
        DeleteQueryMetadata deleteQueryMetadata = new DeleteQueryMetadata();
        deleteQueryMetadata.setDomainTypeName(domainTypeName);

        return new DeleteQueryConditionsBuilder(deleteQueryMetadata);
    }

    public static DeleteQueriesBuilder delete(DeleteQueryMetadata... deleteQueryMetadataArray) {
        return new DeleteQueriesBuilder(deleteQueryMetadataArray);
    }

}
