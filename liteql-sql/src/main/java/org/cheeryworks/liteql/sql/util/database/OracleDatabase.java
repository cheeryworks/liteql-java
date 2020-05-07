package org.cheeryworks.liteql.sql.util.database;

public class OracleDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String getValidationQuery() {
        return "select 1 from dual";
    }
}
