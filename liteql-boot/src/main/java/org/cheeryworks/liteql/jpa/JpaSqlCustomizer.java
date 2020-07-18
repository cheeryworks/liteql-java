package org.cheeryworks.liteql.jpa;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.Trait;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.Map;

public class JpaSqlCustomizer implements SqlCustomizer {

    private Map<TypeName, String> tableNames = new HashMap<>();

    public JpaSqlCustomizer(EntityManagerFactory entityManagerFactory) {
        for (EntityType entityType : entityManagerFactory.getMetamodel().getEntities()) {
            Table table = AnnotationUtils.findAnnotation(entityType.getJavaType(), Table.class);
            TypeName typeName = Trait.getTypeName(entityType.getJavaType());

            if (typeName != null && table != null) {
                tableNames.put(typeName, table.name());
            }
        }
    }

    @Override
    public String getTableName(TypeName domainTypeName) {
        String tableName = tableNames.get(domainTypeName);

        if (StringUtils.isBlank(tableName)) {
            return SqlCustomizer.super.getTableName(domainTypeName);
        }

        return tableName;
    }

}
