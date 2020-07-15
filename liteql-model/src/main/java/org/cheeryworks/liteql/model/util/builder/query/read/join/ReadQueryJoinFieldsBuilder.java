package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryFieldMetadata;

public class ReadQueryJoinFieldsBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinFieldsBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinConditionsBuilder fields(ReadQueryFieldMetadata... readQueryFieldMetadataArray) {
        for (ReadQueryFieldMetadata readQueryFieldMetadata : readQueryFieldMetadataArray) {
            this.readQueryJoinMetadata.getFields().add(
                    new FieldDefinition(readQueryFieldMetadata.getName(), readQueryFieldMetadata.getAlias()));
        }

        return new ReadQueryJoinConditionsBuilder(this.readQueryJoinMetadata);
    }

}
