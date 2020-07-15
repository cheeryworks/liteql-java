package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryFieldMetadata;

public class ReadQueryJoinFieldsBuilder {

    private ReadQueryJoinMetadata liteQLReadQueryJoin;

    public ReadQueryJoinFieldsBuilder(ReadQueryJoinMetadata liteQLReadQueryJoin) {
        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public ReadQueryJoinConditionsBuilder fields(ReadQueryFieldMetadata... readQueryFieldMetadataArray) {
        for (ReadQueryFieldMetadata readQueryFieldMetadata : readQueryFieldMetadataArray) {
            this.liteQLReadQueryJoin.getFields().add(
                    new FieldDefinition(readQueryFieldMetadata.getName(), readQueryFieldMetadata.getAlias()));
        }

        return new ReadQueryJoinConditionsBuilder(this.liteQLReadQueryJoin);
    }

}
