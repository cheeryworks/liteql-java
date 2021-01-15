package org.cheeryworks.liteql.skeleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return logger;
    }

}
