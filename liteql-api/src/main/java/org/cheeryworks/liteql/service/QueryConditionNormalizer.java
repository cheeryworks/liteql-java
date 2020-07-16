package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.QueryContext;

import java.util.List;

public interface QueryConditionNormalizer {

    void normalize(List<QueryCondition> conditions, QueryContext queryContext);

}
