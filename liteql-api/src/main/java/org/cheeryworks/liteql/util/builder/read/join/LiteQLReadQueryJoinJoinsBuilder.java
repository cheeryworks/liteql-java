package org.cheeryworks.liteql.util.builder.read.join;

public class LiteQLReadQueryJoinJoinsBuilder extends LiteQLReadQueryJoinEndBuilder {

    private LiteQLReadQueryJoin liteQLReadQueryJoin;

    public LiteQLReadQueryJoinJoinsBuilder(LiteQLReadQueryJoin liteQLReadQueryJoin) {
        super(liteQLReadQueryJoin);

        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public LiteQLReadQueryJoinEndBuilder joins(LiteQLReadQueryJoin... liteQLReadQueryJoins) {
        this.liteQLReadQueryJoin.setLiteQLReadQueryJoins(liteQLReadQueryJoins);

        return new LiteQLReadQueryJoinEndBuilder(this.liteQLReadQueryJoin);
    }

}
