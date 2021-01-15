package org.cheeryworks.liteql.skeleton.util.query.builder.read.join;

import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.ReadQueryFieldMetadata;

public class ReadQueryJoinFieldsBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinFieldsBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinJoinConditionsBuilder fields(ReadQueryFieldMetadata... readQueryFieldMetadataArray) {
        for (ReadQueryFieldMetadata readQueryFieldMetadata : readQueryFieldMetadataArray) {
            this.readQueryJoinMetadata.getFields().add(
                    new FieldDefinition(readQueryFieldMetadata.getName(), readQueryFieldMetadata.getAlias()));
        }

        return new ReadQueryJoinJoinConditionsBuilder(this.readQueryJoinMetadata);
    }

}
