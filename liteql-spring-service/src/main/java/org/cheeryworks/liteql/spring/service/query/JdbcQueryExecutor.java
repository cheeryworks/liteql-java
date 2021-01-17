package org.cheeryworks.liteql.spring.service.query;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResult;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.service.query.sql.SqlQueryExecutor;
import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;
import org.cheeryworks.liteql.spring.service.AbstractJdbcExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcQueryExecutor extends AbstractJdbcExecutor implements SqlQueryExecutor {

    public JdbcQueryExecutor(LiteQLProperties liteQLProperties, JdbcTemplate jdbcTemplate) {
        super(liteQLProperties, jdbcTemplate);
    }

    @Override
    public ReadResults read(SqlReadQuery sqlReadQuery) {
        List<Map<String, Object>> results
                = getJdbcTemplate().queryForList(sqlReadQuery.getSql(), sqlReadQuery.getSqlParameters());

        List<ReadResult> readResults = new ArrayList<>();

        for (Map<String, Object> result : results) {
            readResults.add(new ReadResult(result));
        }
        
        return new ReadResults(readResults);
    }

}
