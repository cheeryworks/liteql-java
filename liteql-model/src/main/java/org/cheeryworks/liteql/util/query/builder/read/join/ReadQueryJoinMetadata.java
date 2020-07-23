package org.cheeryworks.liteql.util.query.builder.read.join;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.util.query.builder.read.AbstractReadQueryMetadata;

public class ReadQueryJoinMetadata extends AbstractReadQueryMetadata {

    public static ReadQueryJoinFieldsBuilder join(TypeName domainTypeName) {
        ReadQueryJoinMetadata readQueryJoinMetadata = new ReadQueryJoinMetadata();

        readQueryJoinMetadata.setDomainTypeName(domainTypeName);

        return new ReadQueryJoinFieldsBuilder(readQueryJoinMetadata);
    }

}
