package org.cheeryworks.liteql.model.query.page;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractPageable that = (AbstractPageable) o;

        if (page != that.page) {
            return false;
        }

        return size == that.size;
    }

    @Override
    public int hashCode() {
        int result = page;
        result = 31 * result + size;
        return result;
    }

}
