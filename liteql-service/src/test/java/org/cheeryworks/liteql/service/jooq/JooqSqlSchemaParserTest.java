package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractSqlTest;
import org.cheeryworks.liteql.model.type.Type;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JooqSqlSchemaParserTest extends AbstractSqlTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSqlSchemaParserTest.class);

    private JooqSqlSchemaParser jooqSqlSchemaParser;

    public JooqSqlSchemaParserTest() {
        super();

        jooqSqlSchemaParser = new JooqSqlSchemaParser(getRepository(), getDatabase());
    }

    @Test
    public void testingRepositoryToSql() {
        logger.info("\n" + jooqSqlSchemaParser.repositoryToSql());
    }

    @Test
    public void testingSchemaToSql() {
        logger.info("\n" + jooqSqlSchemaParser.schemaToSql("liteql_test"));
    }

    @Test
    public void testingTypeToSql() {
        logger.info("\n" + jooqSqlSchemaParser.domainTypeToSql(new Type("liteql_test", "user")));
    }

}
