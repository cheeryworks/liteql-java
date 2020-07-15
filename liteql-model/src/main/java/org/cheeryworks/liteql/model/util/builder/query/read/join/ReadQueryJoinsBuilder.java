package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryMetadata;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryConditionsBuilder;

public class ReadQueryJoinsBuilder extends ReadQueryConditionsBuilder {

    private ReadQueryMetadata readQueryMetadata;

    public ReadQueryJoinsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);

        this.readQueryMetadata = readQueryMetadata;
    }

    public ReadQueryConditionsBuilder joins(ReadQueryJoinMetadata... readQueryJoinMetadataArray) {
        this.readQueryMetadata.setReadQueryJoinMetadataArray(readQueryJoinMetadataArray);

        return new ReadQueryConditionsBuilder(this.readQueryMetadata);
    }

}
