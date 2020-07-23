package org.cheeryworks.liteql.query.read.result;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeReadResults extends LinkedList<TreeReadResult> implements ReadResultsData<TreeReadResult> {

    public TreeReadResults(List<TreeReadResult> source) {
        super(source);
    }

    @Override
    public List<TreeReadResult> getData() {
        return this.stream().collect(Collectors.toList());
    }
}
