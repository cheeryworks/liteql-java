package org.cheeryworks.liteql;

import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.repository.PathMatchingResourceRepository;
import org.cheeryworks.liteql.service.repository.Repository;

public abstract class AbstractSqlTest extends BaseTest {

    private Repository repository;

    private Database database;

    public AbstractSqlTest() {
        repository = new PathMatchingResourceRepository(getObjectMapper(), "classpath*:/liteql");

        database = Database.H2;
    }

    public Repository getRepository() {
        return repository;
    }

    public Database getDatabase() {
        return database;
    }

}
