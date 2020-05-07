package org.cheeryworks.liteql.model.query.condition.type;

import org.cheeryworks.liteql.model.enums.StandardConditionType;

public class IntegerConditionType extends AbstractConditionType {

    @Override
    public String getName() {
        return StandardConditionType.Integer.name();
    }

}
