package org.cheeryworks.liteql.spring;

import org.cheeryworks.liteql.model.type.TypeName;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface JpaSchemaService {

    void exportSql(String outputFileAbsolutePath);

    void exportLiteQL(String outputFileAbsolutePath) throws IOException;

    Map<String, Set<TypeName>> getTypeNameWithinSchemas();

}
