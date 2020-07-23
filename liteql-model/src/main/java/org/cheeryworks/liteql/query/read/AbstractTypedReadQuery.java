package org.cheeryworks.liteql.query.read;

import org.cheeryworks.liteql.query.TypedQuery;

public abstract class AbstractTypedReadQuery<T extends AbstractReadQuery>
        extends AbstractReadQuery<T> implements TypedQuery {

}
