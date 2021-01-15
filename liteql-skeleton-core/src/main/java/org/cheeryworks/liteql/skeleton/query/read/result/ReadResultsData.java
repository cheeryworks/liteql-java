package org.cheeryworks.liteql.skeleton.query.read.result;

import java.util.List;

public interface ReadResultsData<T extends ReadResult> {

    List<T> getData();

}
