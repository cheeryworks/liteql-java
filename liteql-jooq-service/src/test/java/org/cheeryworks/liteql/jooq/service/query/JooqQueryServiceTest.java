package org.cheeryworks.liteql.jooq.service.query;

import org.cheeryworks.liteql.skeleton.query.Queries;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.util.FileReader;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

public class JooqQueryServiceTest extends AbstractJooqQueryServiceTest {

    @Test
    public void testingRead() {
        Map<String, String> readQueryJsonFiles = FileReader.readJsonFilesRecursively(
                getClass().getResource("/liteql/liteql_test/queries/read").getPath());

        for (String readQueryInJson : readQueryJsonFiles.values()) {
            ReadQuery readQuery = LiteQL.JacksonJsonUtils.toBean(readQueryInJson, ReadQuery.class);

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
            DeleteQuery deleteQuery = LiteQL.JacksonJsonUtils.toBean(deleteQueryInJson, DeleteQuery.class);

            getQueryService().delete(getQueryContext(), deleteQuery);
        }
    }

    @Test
    public void testingQueriesExecute() {
        Map<String, String> queriesJsonFiles = FileReader.readFiles(
                getClass().getResource("/liteql/liteql_test/queries").getPath(), "json", false);

        for (String queriesJsonFile : queriesJsonFiles.values()) {
            Queries queries = LiteQL.JacksonJsonUtils.toBean(queriesJsonFile, Queries.class);

            Object results = getQueryService().execute(getQueryContext(), queries);

            getLogger().info(LiteQL.JacksonJsonUtils.toJson(results));
        }

        exportAndPrintDdl();
    }

}
