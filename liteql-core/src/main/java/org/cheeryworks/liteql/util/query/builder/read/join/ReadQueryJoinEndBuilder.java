package org.cheeryworks.liteql.util.query.builder.read.join;

public class ReadQueryJoinEndBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinEndBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinMetadata build() {
        return this.readQueryJoinMetadata;
    }

}
