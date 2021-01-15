package org.cheeryworks.liteql.jooq.service.query;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.jooq.service.AbstractJooqExecutor;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResult;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.service.query.sql.SqlQueryExecutor;
import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
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
        Results results = getDslContext().fetchMany(sqlReadQuery.getSql(), sqlReadQuery.getSqlParameters());

        List<ReadResult> readResults = new ArrayList<>();

        for (Result result : results) {
            List<ReadResult> subResultsInMap = result.map((RecordMapper<Record, Map<String, Object>>) record -> {
                Map<String, Object> subResultInMap = new HashMap<>();

                for (org.jooq.Field jooqField : record.fields()) {
                    String fieldName = sqlReadQuery.getFields().get(jooqField.getName().toLowerCase());

                    if (StringUtils.isNotBlank(fieldName)) {
                        subResultInMap.put(
                                sqlReadQuery.getFields().get(jooqField.getName().toLowerCase()),
                                record.getValue(jooqField.getName()));
                    }
                }

                return new ReadResult(subResultInMap);
            });

            readResults.addAll(subResultsInMap);
        }

        return new ReadResults(readResults);
    }

}
