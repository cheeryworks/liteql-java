package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.cheeryworks.liteql.service.query.jooq.JooqSqlQueryParser;
import org.cheeryworks.liteql.sql.SqlReadQuery;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JooqSqlQueryParserTest extends AbstractJooqSqlTest {

    private Logger logger = LoggerFactory.getLogger(JooqSqlQueryParserTest.class);

    private JooqSqlQueryParser jooqSqlQueryParser;

    public JooqSqlQueryParserTest() {
        super();

        jooqSqlQueryParser = new JooqSqlQueryParser(
                getLiteQLProperties(), getSchemaService(), getDslContext(), null);
    }

    @Test
    public void testingGetSqlReadQuery() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {

            ReadQuery readQuery = LiteQLUtil.toBean(getObjectMapper(), readQueryInJson, ReadQuery.class);

            SqlReadQuery sqlQuery = jooqSqlQueryParser.getSqlReadQuery(readQuery);

            logger.info(LiteQLUtil.toJson(getObjectMapper(), sqlQuery));
        }
    }

}
