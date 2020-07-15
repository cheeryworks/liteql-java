package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.sort.QuerySort;

import java.util.Arrays;
import java.util.LinkedList;

public class ReadQuerySortsBuilder extends ReadQueryTypeBuilder {

    private ReadQueryMetadata liteQLReadQuery;

    public ReadQuerySortsBuilder(ReadQueryMetadata liteQLReadQuery) {
        super(liteQLReadQuery);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public ReadQueryTypeBuilder sorts(QuerySort... querySorts) {
        this.liteQLReadQuery.setSorts(new LinkedList<>(Arrays.asList(querySorts)));

        return new ReadQueryTypeBuilder(this.liteQLReadQuery);
    }

}
