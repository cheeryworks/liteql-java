package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.AbstractLiteQLService;

public abstract class AbstractSqlService extends AbstractLiteQLService implements SqlService {

    private SqlCustomizer sqlCustomizer;

    public AbstractSqlService(LiteQLProperties liteQLProperties, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties);

        this.sqlCustomizer = sqlCustomizer;
    }

    @Override
    public SqlCustomizer getSqlCustomizer() {
        return this.sqlCustomizer;
    }

}
