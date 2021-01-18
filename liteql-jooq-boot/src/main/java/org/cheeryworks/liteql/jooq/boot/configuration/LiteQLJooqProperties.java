package org.cheeryworks.liteql.jooq.boot.configuration;

import org.cheeryworks.liteql.spring.boot.configuration.LiteQLSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LiteQLJooqProperties.PREFIX)
public class LiteQLJooqProperties {

    public static final String PREFIX = LiteQLSpringProperties.PREFIX + ".jooq";

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
