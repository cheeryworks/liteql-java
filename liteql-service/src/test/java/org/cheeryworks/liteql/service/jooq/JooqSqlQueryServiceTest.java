package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.util.FileReader;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

public class JooqSqlQueryServiceTest extends AbstractJooqSqlQueryServiceTest {

    @Test
    public void testingRead() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {
            ReadQuery readQuery = LiteQLJsonUtil.toBean(
                    getObjectMapper(), readQueryInJson, ReadQuery.class);

            Object results = getQueryService().read(getQueryContext(), readQuery);

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
                getClass().getResource("/liteql/liteql_test/queries/delete").getPath());

        for (String deleteQueryInJson : deleteQueryJsonFiles.values()) {
            DeleteQuery deleteQuery = LiteQLJsonUtil.toBean(getObjectMapper(), deleteQueryInJson, DeleteQuery.class);

            getQueryService().delete(getQueryContext(), deleteQuery);
        }
    }

    @Test
    public void testingQueriesExecute() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/liteql_test/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQLJsonUtil.toBean(getObjectMapper(), queriesJsonFile, Queries.class);

            Object results = getQueryService().execute(getQueryContext(), queries);

            getLogger().info(LiteQLJsonUtil.toJson(getObjectMapper(), results));
        }

        exportAndPrintDdl();
    }

}
