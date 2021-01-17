package org.cheeryworks.liteql.skeleton.database;

public class H2Database extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.h2.Driver";
    }

}
