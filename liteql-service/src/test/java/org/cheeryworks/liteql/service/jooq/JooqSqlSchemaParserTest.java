package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.service.schema.jooq.JooqSqlSchemaParser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JooqSqlSchemaParserTest extends AbstractJooqSqlTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSqlSchemaParserTest.class);

    private JooqSqlSchemaParser jooqSqlSchemaParser;

    public JooqSqlSchemaParserTest() {
        super();

        jooqSqlSchemaParser = new JooqSqlSchemaParser(getLiteQLProperties(), getSchemaService(), getDslContext(), null);
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
        logger.info("\n" + jooqSqlSchemaParser.domainTypeToSql(new TypeName("liteql_test", "user")));
    }

}
