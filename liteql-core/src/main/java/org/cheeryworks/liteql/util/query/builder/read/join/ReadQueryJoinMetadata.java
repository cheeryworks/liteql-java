package org.cheeryworks.liteql.util.query.builder.read.join;

import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.util.LiteQL;
import org.cheeryworks.liteql.util.query.builder.read.AbstractReadQueryMetadata;

public class ReadQueryJoinMetadata extends AbstractReadQueryMetadata {

    public static ReadQueryJoinFieldsBuilder join(Class<? extends TraitType> domainType) {
        return join(LiteQL.SchemaUtils.getTypeName(domainType));
    }

    public static ReadQueryJoinFieldsBuilder join(TypeName domainTypeName) {
        ReadQueryJoinMetadata readQueryJoinMetadata = new ReadQueryJoinMetadata();

        readQueryJoinMetadata.setDomainTypeName(domainTypeName);

        return new ReadQueryJoinFieldsBuilder(readQueryJoinMetadata);
    }

}
