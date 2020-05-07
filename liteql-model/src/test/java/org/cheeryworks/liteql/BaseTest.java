package org.cheeryworks.liteql;

import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Logger getLogger() {
        return logger;
    }

    @BeforeAll
    public static void setProperties() {
        System.setProperty(LiteQLConstants.DIAGNOSTIC_ENABLED_KEY, Boolean.TRUE.toString());
    }

}
