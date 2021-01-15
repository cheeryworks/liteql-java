package org.cheeryworks.liteql.skeleton.util.query.builder;

import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;
import org.cheeryworks.liteql.skeleton.schema.TraitType;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.skeleton.util.query.builder.delete.DeleteQueriesBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.delete.DeleteQueryConditionsBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.delete.DeleteQueryMetadata;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.ReadQueryFieldsBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.ReadQueryMetadata;
import org.cheeryworks.liteql.skeleton.util.query.builder.save.SaveQueriesBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.save.SaveQueryFieldsBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.save.SaveQueryMetadata;

public class QueryBuilder {

    public static ReadQueryFieldsBuilder read(Class<? extends TraitType> domainType) {
        return read(LiteQL.SchemaUtils.getTypeName(domainType));
    }

    public static ReadQueryFieldsBuilder read(TypeName domainTypeName) {
        ReadQueryMetadata readQueryMetadata = new ReadQueryMetadata();

        readQueryMetadata.setDomainTypeName(domainTypeName);

        return new ReadQueryFieldsBuilder(readQueryMetadata);
    }

    public static SaveQueryFieldsBuilder<CreateQuery> create(Class<? extends TraitType> domainType) {
        return create(LiteQL.SchemaUtils.getTypeName(domainType));
    }

    public static SaveQueryFieldsBuilder<CreateQuery> create(TypeName domainTypeName) {
        SaveQueryMetadata<CreateQuery> saveQueryMetadata = new SaveQueryMetadata<>(new CreateQuery());

        saveQueryMetadata.setDomainTypeName(domainTypeName);

        return new SaveQueryFieldsBuilder<>(saveQueryMetadata);
    }

    public static SaveQueryFieldsBuilder<UpdateQuery> update(Class<? extends TraitType> domainType) {
        return update(LiteQL.SchemaUtils.getTypeName(domainType));
    }

    public static SaveQueryFieldsBuilder<UpdateQuery> update(TypeName domainTypeName) {
        SaveQueryMetadata<UpdateQuery> saveQueryMetadata = new SaveQueryMetadata<>(new UpdateQuery());

        saveQueryMetadata.setDomainTypeName(domainTypeName);

        return new SaveQueryFieldsBuilder<>(saveQueryMetadata);
    }

    public static SaveQueriesBuilder save(SaveQueryMetadata... saveQueryMetadataArray) {
        return new SaveQueriesBuilder(saveQueryMetadataArray);
    }

    public static DeleteQueryConditionsBuilder delete(Class<? extends TraitType> domainType) {
        return delete(LiteQL.SchemaUtils.getTypeName(domainType));
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
