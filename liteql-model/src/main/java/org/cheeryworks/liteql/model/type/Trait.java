package org.cheeryworks.liteql.model.type;

import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;

public interface Trait extends Serializable {

    static TypeName getTypeName(Class traitJavaType) {
        ResourceDefinition resourceDefinition
                = AnnotationUtils.findAnnotation(traitJavaType, ResourceDefinition.class);

        if (resourceDefinition != null) {
            TypeName typeName = new TypeName();
            typeName.setSchema(resourceDefinition.namespace());
            typeName.setName(StringUtil.camelNameToLowerDashConnectedLowercaseName(traitJavaType.getSimpleName()));

            return typeName;
        }

        return null;
    }

}
