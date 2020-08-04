package org.cheeryworks.liteql.util.query.builder.read.join;

import org.cheeryworks.liteql.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.util.query.builder.read.ReadQueryFieldMetadata;

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
