package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.AbstractLiteQLService;

public abstract class AbstractSqlService extends AbstractLiteQLService implements SqlService {

    private SqlCustomizer sqlCustomizer = new DefaultSqlCustomizer();

    public AbstractSqlService(LiteQLProperties liteQLProperties, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties);

        if (sqlCustomizer != null) {
            this.sqlCustomizer = sqlCustomizer;
        }
    }

    @Override
    public SqlCustomizer getSqlCustomizer() {
        return this.sqlCustomizer;
    }

}
