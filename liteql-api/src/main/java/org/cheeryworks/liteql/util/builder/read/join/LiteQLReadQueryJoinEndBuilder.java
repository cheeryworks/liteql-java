package org.cheeryworks.liteql.util.builder.read.join;

public class LiteQLReadQueryJoinEndBuilder {

    private LiteQLReadQueryJoin liteQLReadQueryJoin;

    public LiteQLReadQueryJoinEndBuilder(LiteQLReadQueryJoin liteQLReadQueryJoin) {
        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public LiteQLReadQueryJoin build() {
        return this.liteQLReadQueryJoin;
    }

}
