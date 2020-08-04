package org.cheeryworks.liteql.query.read.result;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadResults extends LinkedList<ReadResult> implements ReadResultsData<ReadResult> {

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

    public List<ReadResult> getData() {
        return this.stream().collect(Collectors.toList());
    }
}
