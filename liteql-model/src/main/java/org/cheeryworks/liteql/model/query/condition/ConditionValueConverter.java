package org.cheeryworks.liteql.model.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

public interface ConditionValueConverter {

    ConditionType getConditionType();

    Object convert(Object value);

}
