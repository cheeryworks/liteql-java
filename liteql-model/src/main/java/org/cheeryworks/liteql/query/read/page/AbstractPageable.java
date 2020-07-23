package org.cheeryworks.liteql.query.read.page;

public abstract class AbstractPageable implements Pageable {

    private final int page;

    private final int size;

    public AbstractPageable(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }

        this.page = page;

        this.size = size;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

}
