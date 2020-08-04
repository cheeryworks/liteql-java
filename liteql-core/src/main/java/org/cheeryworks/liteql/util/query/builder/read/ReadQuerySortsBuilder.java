package org.cheeryworks.liteql.util.query.builder.read;

import org.cheeryworks.liteql.query.read.sort.QuerySort;

import java.util.Arrays;
import java.util.LinkedList;

public class ReadQuerySortsBuilder extends ReadQueryBuilder {

    public ReadQuerySortsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);
    }

    public ReadQueryBuilder sorts(QuerySort... querySorts) {
        getReadQueryMetadata().setSorts(new LinkedList<>(Arrays.asList(querySorts)));

        return this;
    }

}
