package org.cheeryworks.liteql.sql.jooq.util;

import org.cheeryworks.liteql.model.query.page.Pageable;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.util.DatabaseTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;

public abstract class JOOQPageUtil {

    public static String getPageSql(
            Database database, SelectConditionStep selectConditionStep,
            Pageable pageable) {
        return getPageSql(database, selectConditionStep, pageable.getPage() * pageable.getSize(), pageable.getSize());
    }

    public static String getPageSql(Database database, SelectLimitStep selectLimitStep, int start, int limit) {
        if (DatabaseTypeUtil.isOracle(database)) {
            String sql = selectLimitStep.getSQL();
            return "select t1.* from (select rownum as rn, t.* from (" + sql + ") t ) t1 where rn > "
                    + start + " and rn <= " + (start + limit);
        } else if (DatabaseTypeUtil.isDB2(database)) {
            String sql = selectLimitStep.getSQL();
            return "select t1.* from (select rownumber() over() AS rn, t.* from (" + sql + ") t ) t1 where rn > "
                    + start + " and rn <= " + (start + limit);
        } else if (DatabaseTypeUtil.isSqlServer(database)) {
            String sql = selectLimitStep.getSQL();

            int orderBySubQueryStartIndex = StringUtils.indexOfIgnoreCase(sql, "order by");
            String orderBySubQuery = "order by (select 1)";

            if (orderBySubQueryStartIndex >= 0) {
                orderBySubQuery = sql.substring(orderBySubQueryStartIndex);
                orderBySubQuery = orderBySubQuery.replaceAll("\\ [^\\ ]*\\.", " t.");
                sql = sql.substring(0, orderBySubQueryStartIndex);
            }

            return "select t1.* from (select row_number() over(" + orderBySubQuery + ") AS rn, t.* from ("
                    + sql + ") t ) t1 where rn > "
                    + start + " and rn <= " + (start + limit);
        }

        selectLimitStep.limit(start, limit);
        return selectLimitStep.getSQL();
    }

}
