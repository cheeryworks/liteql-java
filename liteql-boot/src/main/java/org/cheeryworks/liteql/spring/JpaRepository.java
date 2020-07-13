package org.cheeryworks.liteql.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.service.repository.PathMatchingResourceRepository;

import java.util.Map;
import java.util.Set;

public class JpaRepository extends PathMatchingResourceRepository {

    public JpaRepository(ObjectMapper objectMapper, JpaSchemaService jpaSchemaService, String... locationPatterns) {
        super(objectMapper, locationPatterns);

        Map<String, Set<TypeName>> typeNameWithinSchemas = jpaSchemaService.getTypeNameWithinSchemas();

        for (Map.Entry<String, Set<TypeName>> typeNameWithinSchema : typeNameWithinSchemas.entrySet()) {
            for (TypeName typeName : typeNameWithinSchema.getValue()) {
                addType(typeName);
            }
        }
    }

}
