package org.cheeryworks.liteql.service.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

public interface ConditionValueConverter {

    ConditionType getConditionType();

    Object convert(Object value);

}
