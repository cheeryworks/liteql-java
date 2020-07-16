package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.sort.QuerySort;

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
