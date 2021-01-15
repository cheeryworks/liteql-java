package org.cheeryworks.liteql.skeleton.query.read.page;

import java.io.Serializable;

public interface Pageable extends Serializable {

    int getPage();

    int getSize();

}
