package org.cheeryworks.liteql.sql.util.database;

public class DB2Database extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    public String getValidationQuery() {
        return "select 1 from SYSIBM.SYSDUMMY1";
    }

}
