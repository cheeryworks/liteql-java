package org.cheeryworks.liteql.model.query.page;

import java.io.Serializable;

public interface Pageable extends Serializable {

    int getPage();

    int getSize();

}
