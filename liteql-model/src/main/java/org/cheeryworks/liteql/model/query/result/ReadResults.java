package org.cheeryworks.liteql.model.query.result;

import java.util.LinkedList;
import java.util.List;

public class ReadResults extends LinkedList<ReadResult> {

    private final long total;

    public ReadResults(List<ReadResult> source) {
        this(source, source.size());
    }

    public ReadResults(List<ReadResult> source, long total) {
        super(source);

        this.total = total;
    }


    public long getTotal() {
        return total;
    }
}
