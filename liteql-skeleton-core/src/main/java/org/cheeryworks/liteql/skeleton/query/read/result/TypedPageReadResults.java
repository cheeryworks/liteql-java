package org.cheeryworks.liteql.skeleton.query.read.result;

import org.cheeryworks.liteql.skeleton.query.read.page.AbstractPageable;
import org.cheeryworks.liteql.skeleton.query.read.page.Page;
import org.cheeryworks.liteql.skeleton.schema.TraitType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypedPageReadResults<T extends TraitType> extends AbstractPageable implements Page<T> {

    private final long total;

    private final List<T> data = new ArrayList<>();

    public TypedPageReadResults(List<T> data, int page, int size, long total) {
        super(page, size);

        this.data.addAll(data);
        this.total = total;
    }

    @Override
    public List<T> getData() {
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
