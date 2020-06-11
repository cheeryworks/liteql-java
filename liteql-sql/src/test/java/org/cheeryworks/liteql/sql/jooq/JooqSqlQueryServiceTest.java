package org.cheeryworks.liteql.sql.jooq;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.AbstractDatabaseTest;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.cheeryworks.liteql.service.query.QueryService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class JooqSqlQueryServiceTest extends AbstractDatabaseTest {

    private QueryService queryService;

    public JooqSqlQueryServiceTest() {
        super();

        queryService = new JooqSqlQueryService(getRepository(), getDataSource(), getDatabase());
    }

    @Override
    protected String[] getInitSqls() {
        try {
            JooqSqlSchemaParser jooqSqlSchemaParser = new JooqSqlSchemaParser(getRepository(), getDatabase());

            String schemaSqls = jooqSqlSchemaParser.repositoryToSql().replaceAll("\n", "");

            String[] initSqls = schemaSqls.split(";");

            String dataSqls = IOUtils.toString(
                    getClass().getResourceAsStream("/database/init_data_query_service.sql"),
                    StandardCharsets.UTF_8);

            initSqls = ArrayUtils.addAll(initSqls, dataSqls);

            return initSqls;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Test
    public void testingRead() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {
            ReadQuery readQuery = LiteQLJsonUtil.toBean(
                    readQueryInJson, ReadQuery.class);

            Object results = queryService.read(readQuery);

            if (results instanceof Map) {
                getLogger().info(results.toString());
            } else if (results instanceof Iterable) {
                Iterator<Object> iterator = ((Iterable<Object>) results).iterator();

                while (iterator.hasNext()) {
                    getLogger().info(iterator.next().toString());
                }
            }
        }
    }

    @Test
    public void testingDelete() {
        Map<String, String> deleteQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/queries/delete").getPath());

        for (String deleteQueryInJson : deleteQueryJsonFiles.values()) {
            DeleteQuery deleteQuery = LiteQLJsonUtil.toBean(deleteQueryInJson, DeleteQuery.class);

            queryService.delete(deleteQuery);
        }
    }

    @Test
    public void testingQueriesExecute() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQLJsonUtil.toBean(queriesJsonFile, Queries.class);

            Object results = queryService.execute(queries);

            getLogger().info(LiteQLJsonUtil.toJson(results));
        }

        exportAndPrintDdl();
    }

}
