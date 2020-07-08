package org.cheeryworks.liteql.model.util.builder.delete;

public class LiteQLDeleteQueryTruncatedBuilder extends LiteQLDeleteQueryEndBuilder {

    private LiteQLDeleteQuery liteQLDeleteQuery;

    public LiteQLDeleteQueryTruncatedBuilder(LiteQLDeleteQuery liteQLDeleteQuery) {
        super(liteQLDeleteQuery);

        this.liteQLDeleteQuery = liteQLDeleteQuery;
    }

    public LiteQLDeleteQueryEndBuilder truncated() {
        this.liteQLDeleteQuery.setTruncated(true);

        return new LiteQLDeleteQueryEndBuilder(liteQLDeleteQuery);
    }

}
