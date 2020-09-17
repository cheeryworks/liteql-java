package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.sql.SqlReadQuery;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQL;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JooqQueryParserTest extends AbstractJooqTest {

    private Logger logger = LoggerFactory.getLogger(JooqQueryParserTest.class);

    public JooqQueryParserTest() {
        super();
    }

    @Test
    public void testingGetSqlReadQuery() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {

            ReadQuery readQuery = LiteQL.JacksonJsonUtils.toBean(readQueryInJson, ReadQuery.class);

            SqlReadQuery sqlQuery = getJooqQueryParser().getSqlReadQuery(readQuery);

            logger.info(LiteQL.JacksonJsonUtils.toJson(sqlQuery));
        }
    }

}
