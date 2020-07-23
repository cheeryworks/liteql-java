package org.cheeryworks.liteql.query.condition;

import org.cheeryworks.liteql.query.enums.ConditionType;

public interface ConditionValueConverter {

    ConditionType getConditionType();

    Object convert(Object value);

}
