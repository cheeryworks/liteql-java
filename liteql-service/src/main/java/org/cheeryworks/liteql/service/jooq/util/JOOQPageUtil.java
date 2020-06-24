package org.cheeryworks.liteql.service.jooq.util;

import org.cheeryworks.liteql.model.query.read.page.Pageable;
import org.cheeryworks.liteql.service.enums.Database;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;

public abstract class JOOQPageUtil {

    public static String getPageSql(
            Database database, SelectConditionStep selectConditionStep, Pageable pageable) {
        return getPageSql(database, selectConditionStep, pageable.getPage() * pageable.getSize(), pageable.getSize());
    }

    public static String getPageSql(Database database, SelectLimitStep selectLimitStep, int start, int limit) {
        selectLimitStep.limit(start, limit);

        return selectLimitStep.getSQL();
    }

}
