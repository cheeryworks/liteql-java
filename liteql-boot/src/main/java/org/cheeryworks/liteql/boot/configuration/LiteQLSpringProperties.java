package org.cheeryworks.liteql.boot.configuration;

import org.cheeryworks.liteql.LiteQLProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LiteQLSpringProperties.PREFIX)
public class LiteQLSpringProperties extends LiteQLProperties {

    public static final String PREFIX = "liteql";

}
