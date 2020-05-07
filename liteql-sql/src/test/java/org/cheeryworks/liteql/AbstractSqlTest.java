package org.cheeryworks.liteql;

import org.cheeryworks.liteql.service.repository.FileSystemRepository;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.enums.Database;

public abstract class AbstractSqlTest extends BaseTest {

    private Repository repository;

    private Database database;

    public AbstractSqlTest() {
        repository = new FileSystemRepository(getClass().getResource("/liteql/").getPath());

        database = Database.H2;
    }

    public Repository getRepository() {
        return repository;
    }

    public Database getDatabase() {
        return database;
    }

}
