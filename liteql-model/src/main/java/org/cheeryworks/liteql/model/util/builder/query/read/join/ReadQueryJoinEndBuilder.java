package org.cheeryworks.liteql.model.util.builder.query.read.join;

public class ReadQueryJoinEndBuilder {

    private ReadQueryJoinMetadata liteQLReadQueryJoin;

    public ReadQueryJoinEndBuilder(ReadQueryJoinMetadata liteQLReadQueryJoin) {
        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public ReadQueryJoinMetadata build() {
        return this.liteQLReadQueryJoin;
    }

}
