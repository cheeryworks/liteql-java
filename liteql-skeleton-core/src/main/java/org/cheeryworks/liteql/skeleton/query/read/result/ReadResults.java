package org.cheeryworks.liteql.skeleton.query.read.result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadResults extends ArrayList<ReadResult> implements ReadResultsData<ReadResult> {

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
