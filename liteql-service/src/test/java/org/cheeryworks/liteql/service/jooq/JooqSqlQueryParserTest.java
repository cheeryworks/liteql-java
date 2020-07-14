package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.service.query.SqlReadQuery;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JooqSqlQueryParserTest extends AbstractJooqSqlTest {

    private Logger logger = LoggerFactory.getLogger(JooqSqlQueryParserTest.class);

    private JooqSqlQueryParser jooqSqlQueryParser;

    public JooqSqlQueryParserTest() {
        super();

        jooqSqlQueryParser = new JooqSqlQueryParser(getRepository(), getDslContext(), null);
    }

    @Test
    public void testingGetSqlReadQuery() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {

            ReadQuery readQuery = LiteQLJsonUtil.toBean(getObjectMapper(), readQueryInJson, ReadQuery.class);

            SqlReadQuery sqlQuery = jooqSqlQueryParser.getSqlReadQuery(readQuery);

            logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), sqlQuery));
        }
    }

}
