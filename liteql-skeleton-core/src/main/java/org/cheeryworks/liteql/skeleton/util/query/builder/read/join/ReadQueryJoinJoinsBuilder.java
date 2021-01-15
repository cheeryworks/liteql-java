package org.cheeryworks.liteql.skeleton.util.query.builder.read.join;

public class ReadQueryJoinJoinsBuilder extends ReadQueryJoinEndBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinJoinsBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        super(readQueryJoinMetadata);

        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinEndBuilder joins(ReadQueryJoinMetadata... readQueryJoinMetadataArray) {
        this.readQueryJoinMetadata.setReadQueryJoinMetadataArray(readQueryJoinMetadataArray);

        return new ReadQueryJoinEndBuilder(this.readQueryJoinMetadata);
    }

}
