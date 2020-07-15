package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.sort.QuerySort;

import java.util.Arrays;
import java.util.LinkedList;

public class ReadQuerySortsBuilder extends ReadQueryTypeBuilder {

    private ReadQueryMetadata readQueryMetadata;

    public ReadQuerySortsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);

        this.readQueryMetadata = readQueryMetadata;
    }

    public ReadQueryTypeBuilder sorts(QuerySort... querySorts) {
        this.readQueryMetadata.setSorts(new LinkedList<>(Arrays.asList(querySorts)));

        return new ReadQueryTypeBuilder(this.readQueryMetadata);
    }

}
