package org.cheeryworks.liteql.model.util.builder.read.join;

import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.builder.read.AbstractLiteQLReadQuery;

public class LiteQLReadQueryJoin extends AbstractLiteQLReadQuery {

    public static LiteQLReadQueryJoinFieldsBuilder join(TypeName domainTypeName) {
        LiteQLReadQueryJoin liteQLReadQueryJoin = new LiteQLReadQueryJoin();

        liteQLReadQueryJoin.setDomainTypeName(domainTypeName);

        return new LiteQLReadQueryJoinFieldsBuilder(liteQLReadQueryJoin);
    }

}
