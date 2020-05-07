package org.cheeryworks.liteql.model.query.result;

import org.cheeryworks.liteql.model.query.page.AbstractPageable;
import org.cheeryworks.liteql.model.query.page.Page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PageReadResults extends AbstractPageable implements Page<ReadResult> {

    private final long total;

    private final List<ReadResult> data = new ArrayList<ReadResult>();

    public PageReadResults(List<ReadResult> data, int page, int size, long total) {
        super(page, size);

        this.data.addAll(data);
        this.total = total;
    }

    @Override
    public List<ReadResult> getData() {
        return this.data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public int getTotalPage() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    @Override
    public Iterator iterator() {
        return data.iterator();
    }

}
