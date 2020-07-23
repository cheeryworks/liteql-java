package org.cheeryworks.liteql.schema;

import org.cheeryworks.liteql.schema.annotation.ResourceDefinition;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;

public interface Trait extends Serializable {

    static TypeName getTypeName(Class traitJavaType) {
        ResourceDefinition resourceDefinition
                = AnnotationUtils.findAnnotation(traitJavaType, ResourceDefinition.class);

        if (resourceDefinition != null) {
            TypeName typeName = new TypeName();
            typeName.setSchema(resourceDefinition.schema());
            typeName.setName(LiteQLUtil.camelNameToLowerDashConnectedLowercaseName(traitJavaType.getSimpleName()));

            return typeName;
        }

        return null;
    }

}
