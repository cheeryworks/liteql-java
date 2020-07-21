package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.model.query.read.result.ReadResult;
import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.service.jooq.AbstractJooqSqlExecutor;
import org.cheeryworks.liteql.service.query.sql.SqlQueryExecutor;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
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

public class JooqSqlQueryExecutor extends AbstractJooqSqlExecutor implements SqlQueryExecutor {

    public JooqSqlQueryExecutor(DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(dslContext, sqlCustomizer);
    }

    @Override
    public ReadResults read(TypeName domainTypeName, String sql, Map<String, Field> fields, Object[] parameters) {
        ResultQuery resultQuery = getDslContext().resultQuery(sql, parameters);

        Results results = getDslContext().fetchMany(resultQuery);

        List<ReadResult> readResults = new ArrayList<>();

        for (Result result : results) {
            List<ReadResult> subResultsInMap = result.map((RecordMapper<Record, Map<String, Object>>) record -> {
                Map<String, Object> subResultInMap = new HashMap<>();

                for (org.jooq.Field jooqField : record.fields()) {
                    subResultInMap.put(
                            getSqlCustomizer().getFieldName(domainTypeName, jooqField.getName()),
                            record.getValue(jooqField.getName()));
                }

                return new ReadResult(subResultInMap);
            });

            readResults.addAll(subResultsInMap);
        }

        return new ReadResults(readResults);
    }

}