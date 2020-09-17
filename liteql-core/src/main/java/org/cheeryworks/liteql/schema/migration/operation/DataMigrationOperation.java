package org.cheeryworks.liteql.schema.migration.operation;

import org.cheeryworks.liteql.query.Queries;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;

public class DataMigrationOperation extends AbstractMigrationOperation {

    private Queries queries;

    public DataMigrationOperation() {
        super(MigrationOperationType.DATA_MIGRATION);
    }

    public Queries getQueries() {
        return queries;
    }

    public void setQueries(Queries queries) {
        this.queries = queries;
    }

}
