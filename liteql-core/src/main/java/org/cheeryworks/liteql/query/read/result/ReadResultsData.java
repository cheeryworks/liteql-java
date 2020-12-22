package org.cheeryworks.liteql.query.read.result;

import java.util.List;

public interface ReadResultsData<T extends ReadResult> {

    List<T> getData();

}
