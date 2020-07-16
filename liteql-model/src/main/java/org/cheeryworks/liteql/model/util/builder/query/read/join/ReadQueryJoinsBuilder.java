package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryConditionsBuilder;
import org.cheeryworks.liteql.model.util.builder.query.read.ReadQueryMetadata;

public class ReadQueryJoinsBuilder extends ReadQueryConditionsBuilder {

    public ReadQueryJoinsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);
    }

    public ReadQueryConditionsBuilder joins(ReadQueryJoinMetadata... readQueryJoinMetadataArray) {
        getReadQueryMetadata().setReadQueryJoinMetadataArray(readQueryJoinMetadataArray);

        return this;
    }

}
