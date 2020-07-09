package org.cheeryworks.liteql.model.util;

import org.cheeryworks.liteql.model.type.TypeName;

public abstract class LiteQLUtil {

    public static TypeName getTypeName(String domainTypeName) {
        String[] typeNameParts = domainTypeName.split("\\.");

        TypeName typeName = new TypeName();
        typeName.setSchema(typeNameParts[0]);
        typeName.setName(typeNameParts[1]);

        return typeName;
    }

}
