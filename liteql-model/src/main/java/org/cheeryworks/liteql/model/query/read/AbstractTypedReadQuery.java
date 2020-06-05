package org.cheeryworks.liteql.model.query.read;

import org.cheeryworks.liteql.model.query.TypedQuery;

public abstract class AbstractTypedReadQuery<T extends AbstractReadQuery>
        extends AbstractReadQuery<T> implements TypedQuery {

}
