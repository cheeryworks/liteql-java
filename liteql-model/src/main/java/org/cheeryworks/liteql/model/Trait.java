package org.cheeryworks.liteql.model;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.util.LiteQLUtil;

import java.io.Serializable;

public interface Trait extends Serializable {

    static TypeName getTypeName(Class<?> traitJavaType) {
        ResourceDefinition resourceDefinition = traitJavaType.getAnnotation(ResourceDefinition.class);

        if (resourceDefinition != null) {
            TypeName typeName = new TypeName();
            typeName.setSchema(resourceDefinition.schema());
            typeName.setName(LiteQLUtil.camelNameToLowerDashConnectedLowercaseName(traitJavaType.getSimpleName()));

            return typeName;
        }

        return null;
    }

}
