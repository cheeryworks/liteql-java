package org.cheeryworks.liteql.util.query.builder.read;

import org.cheeryworks.liteql.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.util.query.builder.read.join.ReadQueryJoinsBuilder;

public class ReadQueryFieldsBuilder extends ReadQueryJoinsBuilder {

    public ReadQueryFieldsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);
    }

    public ReadQueryJoinsBuilder fields(ReadQueryFieldMetadata... readQueryFieldMetadataArray) {
        for (ReadQueryFieldMetadata readQueryFieldMetadata : readQueryFieldMetadataArray) {
            getReadQueryMetadata().getFields().add(
                    new FieldDefinition(readQueryFieldMetadata.getName(), readQueryFieldMetadata.getAlias()));
        }

        return this;
    }

}
