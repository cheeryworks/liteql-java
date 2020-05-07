package org.cheeryworks.liteql.sql.query.condition;

import org.cheeryworks.liteql.model.query.condition.ConditionType;

public interface ConditionValueConverter<T extends ConditionType> {

    Object convert(T conditionType, Object value);

}
