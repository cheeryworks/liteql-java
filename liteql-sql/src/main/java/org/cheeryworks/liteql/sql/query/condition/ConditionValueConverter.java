package org.cheeryworks.liteql.sql.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

public interface ConditionValueConverter {

    ConditionType getConditionType();

    Object convert(Object value);

}
