package org.cheeryworks.liteql.util.builder.read.join;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.util.builder.read.AbstractLiteQLReadQuery;

public class LiteQLReadQueryJoin extends AbstractLiteQLReadQuery {

    public static LiteQLReadQueryJoinFieldsBuilder join(DomainType domainType) {
        LiteQLReadQueryJoin liteQLReadQueryJoin = new LiteQLReadQueryJoin();

        liteQLReadQueryJoin.setDomainType(domainType);

        return new LiteQLReadQueryJoinFieldsBuilder(liteQLReadQueryJoin);
    }

}
