package org.cheeryworks.liteql.model.util.builder.query.read.join;

public class ReadQueryJoinEndBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinEndBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinMetadata build() {
        return this.readQueryJoinMetadata;
    }

}
