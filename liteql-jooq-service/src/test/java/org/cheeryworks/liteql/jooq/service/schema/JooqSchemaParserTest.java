package org.cheeryworks.liteql.jooq.service.schema;

import org.cheeryworks.liteql.jooq.service.AbstractJooqTest;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JooqSchemaParserTest extends AbstractJooqTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSchemaParserTest.class);

    private JooqSchemaParser jooqSchemaParser;

    public JooqSchemaParserTest() {
        super();

        jooqSchemaParser = new JooqSchemaParser(
                getLiteQLProperties(), getSchemaService(), getSqlCustomizer(), getDslContext());
    }

    @Test
    public void testingRepositoryToSql() {
        logger.info("\n" + jooqSchemaParser.schemaToSql());
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
