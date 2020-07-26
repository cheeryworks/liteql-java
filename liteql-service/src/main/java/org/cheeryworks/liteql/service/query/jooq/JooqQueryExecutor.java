package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.query.read.result.ReadResult;
import org.cheeryworks.liteql.query.read.result.ReadResults;
import org.cheeryworks.liteql.service.jooq.AbstractJooqExecutor;
import org.cheeryworks.liteql.service.query.sql.SqlQueryExecutor;
import org.cheeryworks.liteql.sql.SqlReadQuery;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JooqQueryExecutor extends AbstractJooqExecutor implements SqlQueryExecutor {

    public JooqQueryExecutor(LiteQLProperties liteQLProperties, DSLContext dslContext) {
        super(liteQLProperties, dslContext);
    }

    @Override
    public ReadResults read(SqlReadQuery sqlReadQuery) {
        ResultQuery resultQuery = getDslContext().resultQuery(sqlReadQuery.getSql(), sqlReadQuery.getSqlParameters());

        Results results = getDslContext().fetchMany(resultQuery);

        List<ReadResult> readResults = new ArrayList<>();

        for (Result result : results) {
            List<ReadResult> subResultsInMap = result.map((RecordMapper<Record, Map<String, Object>>) record -> {
                Map<String, Object> subResultInMap = new HashMap<>();

                for (org.jooq.Field jooqField : record.fields()) {
                    subResultInMap.put(
                            sqlReadQuery.getFields().get(jooqField.getName().toLowerCase()),
                            record.getValue(jooqField.getName()));
                }

                return new ReadResult(subResultInMap);
            });

            readResults.addAll(subResultsInMap);
        }

        return new ReadResults(readResults);
    }

}
