package org.cheeryworks.liteql.sql.jooq;

import org.cheeryworks.liteql.model.query.result.ReadResult;
import org.cheeryworks.liteql.model.query.result.ReadResults;
import org.cheeryworks.liteql.model.type.DomainTypeField;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.query.SqlQueryExecutor;
import org.cheeryworks.liteql.sql.util.SqlQueryServiceUtil;
import org.jooq.BatchBindStep;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JooqSqlQueryExecutor extends AbstractJooqSqlExecutor implements SqlQueryExecutor {

    public JooqSqlQueryExecutor(DataSource dataSource, Database database) {
        super(dataSource, database);
    }

    @Override
    public long count(String sql, Object[] parameters) {
        ResultQuery resultQuery = getDslContext().resultQuery(sql, parameters);

        long count = (Long) getDslContext().fetchValue(resultQuery);

        return count;
    }

    @Override
    public ReadResults read(String sql, Map<String, DomainTypeField> fields, Object[] parameters) {
        ResultQuery resultQuery = getDslContext().resultQuery(sql, parameters);

        Results results = getDslContext().fetchMany(resultQuery);

        List<ReadResult> readResults = new ArrayList<>();

        for (Result result : results) {
            List<ReadResult> subResultsInMap = result.map((RecordMapper<Record, Map<String, Object>>) record -> {
                Map<String, Object> subResultInMap = new HashMap<>();

                for (Field jooqField : record.fields()) {
                    subResultInMap.put(
                            SqlQueryServiceUtil.getFieldNameByColumnName(jooqField.getName()),
                            record.getValue(jooqField.getName()));
                }

                return new ReadResult(subResultInMap);
            });

            readResults.addAll(subResultsInMap);
        }

        return new ReadResults(readResults);
    }

    @Override
    public void execute(String sql, Object[] parameters) {
        getDslContext().execute(sql, parameters);
    }

    @Override
    public void executeBatch(String sql, List<Object[]> parametersList) {
        BatchBindStep batchBindStep = getDslContext().batch(sql);

        for (Object[] parameters : parametersList) {
            batchBindStep.bind(parameters);
        }

        batchBindStep.execute();
    }

    @Override
    public void executeNamedBatch(String sql, List<Map<String, Object>> parametersList) {
        BatchBindStep batchBindStep = getDslContext().batch(sql);

        for (Map<String, Object> parameters : parametersList) {
            batchBindStep.bind(parameters);
        }

        batchBindStep.execute();
    }

}
