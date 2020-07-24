package org.cheeryworks.liteql.query.read;

import org.cheeryworks.liteql.query.TypedQuery;
import org.cheeryworks.liteql.query.read.result.ReadResults;

public abstract class AbstractTypedReadQuery<T extends AbstractTypedReadQuery, R>
        extends AbstractReadQuery<T> implements TypedQuery {

    public abstract R getResult(ReadResults readResults);

}
