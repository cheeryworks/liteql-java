package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.read.sort.QuerySort;

import java.util.Arrays;

public class ReadQuerySortsBuilder extends ReadQueryBuilder {

    public ReadQuerySortsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);
    }

    public ReadQueryBuilder sorts(QuerySort... querySorts) {
        getReadQueryMetadata().setSorts(Arrays.asList(querySorts));

        return this;
    }

}
