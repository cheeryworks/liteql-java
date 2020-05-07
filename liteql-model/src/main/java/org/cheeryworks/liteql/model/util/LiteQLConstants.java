package org.cheeryworks.liteql.model.util;

import org.apache.commons.lang3.BooleanUtils;

public abstract class LiteQLConstants {

    public static final String DIAGNOSTIC_ENABLED_KEY = "diagnostic.enabled";

    public static final boolean DIAGNOSTIC_ENABLED;

    static {
        DIAGNOSTIC_ENABLED = BooleanUtils.toBoolean(System.getProperty(DIAGNOSTIC_ENABLED_KEY));
    }

}
