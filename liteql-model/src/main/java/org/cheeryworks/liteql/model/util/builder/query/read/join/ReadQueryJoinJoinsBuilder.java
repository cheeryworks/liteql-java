package org.cheeryworks.liteql.model.util.builder.query.read.join;

public class ReadQueryJoinJoinsBuilder extends ReadQueryJoinEndBuilder {

    private ReadQueryJoinMetadata liteQLReadQueryJoin;

    public ReadQueryJoinJoinsBuilder(ReadQueryJoinMetadata liteQLReadQueryJoin) {
        super(liteQLReadQueryJoin);

        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public ReadQueryJoinEndBuilder joins(ReadQueryJoinMetadata... liteQLReadQueryJoins) {
        this.liteQLReadQueryJoin.setLiteQLReadQueryJoins(liteQLReadQueryJoins);

        return new ReadQueryJoinEndBuilder(this.liteQLReadQueryJoin);
    }

}
