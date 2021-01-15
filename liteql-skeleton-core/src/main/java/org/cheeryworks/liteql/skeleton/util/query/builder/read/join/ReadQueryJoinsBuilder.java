package org.cheeryworks.liteql.skeleton.util.query.builder.read.join;

import org.cheeryworks.liteql.skeleton.util.query.builder.read.ReadQueryConditionsBuilder;
import org.cheeryworks.liteql.skeleton.util.query.builder.read.ReadQueryMetadata;

public class ReadQueryJoinsBuilder extends ReadQueryConditionsBuilder {

    public ReadQueryJoinsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);
    }

    public ReadQueryConditionsBuilder joins(ReadQueryJoinMetadata... readQueryJoinMetadataArray) {
        getReadQueryMetadata().setReadQueryJoinMetadataArray(readQueryJoinMetadataArray);

        return this;
    }

}
