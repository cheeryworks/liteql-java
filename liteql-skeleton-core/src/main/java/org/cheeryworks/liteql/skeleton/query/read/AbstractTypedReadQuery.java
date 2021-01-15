package org.cheeryworks.liteql.skeleton.query.read;

import org.cheeryworks.liteql.skeleton.query.TypedQuery;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;

public abstract class AbstractTypedReadQuery<T extends AbstractTypedReadQuery, R>
        extends AbstractReadQuery<T> implements TypedQuery {

    public abstract R getResult(ReadResults readResults);

}
