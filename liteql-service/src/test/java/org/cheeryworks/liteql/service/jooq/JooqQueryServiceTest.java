package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.query.Queries;
import org.cheeryworks.liteql.query.delete.DeleteQuery;
import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.util.FileReader;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

public class JooqQueryServiceTest extends AbstractJooqQueryServiceTest {

    @Test
    public void testingRead() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {
            ReadQuery readQuery = LiteQLUtil.toBean(readQueryInJson, ReadQuery.class);

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
            DeleteQuery deleteQuery = LiteQLUtil.toBean(deleteQueryInJson, DeleteQuery.class);

            getQueryService().delete(getQueryContext(), deleteQuery);
        }
    }

    @Test
    public void testingQueriesExecute() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/liteql_test/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQLUtil.toBean(queriesJsonFile, Queries.class);

            Object results = getQueryService().execute(getQueryContext(), queries);

            getLogger().info(LiteQLUtil.toJson(results));
        }

        exportAndPrintDdl();
    }

}