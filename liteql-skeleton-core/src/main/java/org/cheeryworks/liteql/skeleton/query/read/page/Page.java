package org.cheeryworks.liteql.skeleton.query.read.page;

import java.util.List;

public interface Page<T> extends Pageable, Iterable<T> {

    List<T> getData();

    int getCount();

    long getTotal();

    int getTotalPage();

}
