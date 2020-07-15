package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.model.util.builder.query.read.join.ReadQueryJoinsBuilder;

public class ReadQueryFieldsBuilder {

    private ReadQueryMetadata liteQLReadQuery;

    public ReadQueryFieldsBuilder(ReadQueryMetadata liteQLReadQuery) {
        this.liteQLReadQuery = liteQLReadQuery;
    }

    public ReadQueryJoinsBuilder fields(ReadQueryFieldMetadata... readQueryFieldMetadataArray) {
        for (ReadQueryFieldMetadata readQueryFieldMetadata : readQueryFieldMetadataArray) {
            this.liteQLReadQuery.getFields().add(
                    new FieldDefinition(readQueryFieldMetadata.getName(), readQueryFieldMetadata.getAlias()));
        }

        return new ReadQueryJoinsBuilder(this.liteQLReadQuery);
    }

}
