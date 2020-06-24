package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractSqlTest;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.cheeryworks.liteql.service.query.SqlReadQuery;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JooqSqlQueryParserTest extends AbstractSqlTest {

    private JooqSqlQueryParser jooqSqlQueryParser;

    public JooqSqlQueryParserTest() {
        super();

        jooqSqlQueryParser = new JooqSqlQueryParser(getRepository(), getDatabase());
    }

    @Test
    public void testingGetSqlReadQuery() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {

            ReadQuery readQuery = LiteQLJsonUtil.toBean(readQueryInJson, ReadQuery.class);

            SqlReadQuery sqlQuery = jooqSqlQueryParser.getSqlReadQuery(readQuery);
        }
    }

}