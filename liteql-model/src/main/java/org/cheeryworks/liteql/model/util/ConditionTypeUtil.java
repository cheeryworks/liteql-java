package org.cheeryworks.liteql.model.util;

import org.cheeryworks.liteql.model.enums.ConditionType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public abstract class ConditionTypeUtil {

    private static final Map<String, ConditionType> CONDITION_TYPES = new HashMap<>();

    static {
        Iterator<ConditionType> conditionTypeIterator = ServiceLoader.load(ConditionType.class).iterator();

        while (conditionTypeIterator.hasNext()) {
            ConditionType conditionType = conditionTypeIterator.next();

            CONDITION_TYPES.put(conditionType.name().toLowerCase(), conditionType);
        }
    }

    public static ConditionType getConditionTypeByName(String conditionTypeName) {
        if (CONDITION_TYPES.containsKey(conditionTypeName)) {
            return CONDITION_TYPES.get(conditionTypeName.toLowerCase());
        }

        throw new IllegalArgumentException("Unsupported condition type: " + conditionTypeName);
    }

    public static ConditionType getConditionTypeByValue(Object value) {
        String conditionTypeName;

        if (value instanceof List) {
            conditionTypeName = ((List) value).get(0).getClass().getSimpleName();
        } else {
            conditionTypeName = value.getClass().getSimpleName().toLowerCase();
        }

        return getConditionTypeByName(conditionTypeName);
    }

}
