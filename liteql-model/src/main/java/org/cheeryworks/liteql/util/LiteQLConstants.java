package org.cheeryworks.liteql.util;

import java.io.File;

public abstract class LiteQLConstants {

    public static final String SPECIFICATION_VERSION = "1.0.0";

    public static final String SCHEMA = "liteql";

    public static final String WORD_CONCAT = "_";

    public static final String NAME_CONCAT = ".";

    public static final String DEFAULT_CUSTOMIZED_CONFIGURATION_PATH
            = System.getProperty("user.home") + File.separator + ".liteql";

    public static final String PLATFORM_VERSION_SPECIFIED_CUSTOMIZED_CONFIGURATION_PATH
            = DEFAULT_CUSTOMIZED_CONFIGURATION_PATH + File.separator + SPECIFICATION_VERSION;

    public static final String LITEQL_PROFILE_KEY = "liteql.profile";

}
