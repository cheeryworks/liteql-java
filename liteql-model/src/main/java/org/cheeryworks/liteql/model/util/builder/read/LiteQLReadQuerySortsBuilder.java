package org.cheeryworks.liteql.model.util.builder.read;

import org.cheeryworks.liteql.model.query.read.sort.QuerySort;

import java.util.Arrays;
import java.util.LinkedList;

public class LiteQLReadQuerySortsBuilder extends LiteQLReadQueryTypeBuilder {

    private LiteQLReadQuery liteQLReadQuery;

    public LiteQLReadQuerySortsBuilder(LiteQLReadQuery liteQLReadQuery) {
        super(liteQLReadQuery);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public LiteQLReadQueryTypeBuilder sorts(QuerySort... querySorts) {
        this.liteQLReadQuery.setSorts(new LinkedList<>(Arrays.asList(querySorts)));

        return new LiteQLReadQueryTypeBuilder(this.liteQLReadQuery);
    }

}
