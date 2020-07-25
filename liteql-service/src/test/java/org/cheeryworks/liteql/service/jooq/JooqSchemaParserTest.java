package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.service.schema.jooq.JooqSchemaParser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JooqSchemaParserTest extends AbstractJooqTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSchemaParserTest.class);

    private JooqSchemaParser jooqSchemaParser;

    public JooqSchemaParserTest() {
        super();

        jooqSchemaParser = new JooqSchemaParser(
                getLiteQLProperties(), getSchemaService(), getDslContext(), getSqlCustomizer());
    }

    @Test
    public void testingRepositoryToSql() {
        logger.info("\n" + jooqSchemaParser.repositoryToSql());
    }

    @Test
    public void testingSchemaToSql() {
        logger.info("\n" + jooqSchemaParser.schemaToSql("liteql_test"));
    }

    @Test
    public void testingTypeToSql() {
        logger.info("\n" + jooqSchemaParser.domainTypeToSql(new TypeName("liteql_test", "user")));
    }

}
