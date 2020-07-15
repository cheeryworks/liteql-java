package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.builder.query.read.AbstractReadQueryMetadata;

public class ReadQueryJoinMetadata extends AbstractReadQueryMetadata {

    public static ReadQueryJoinFieldsBuilder join(TypeName domainTypeName) {
        ReadQueryJoinMetadata readQueryJoinMetadata = new ReadQueryJoinMetadata();

        readQueryJoinMetadata.setDomainTypeName(domainTypeName);

        return new ReadQueryJoinFieldsBuilder(readQueryJoinMetadata);
    }

}
