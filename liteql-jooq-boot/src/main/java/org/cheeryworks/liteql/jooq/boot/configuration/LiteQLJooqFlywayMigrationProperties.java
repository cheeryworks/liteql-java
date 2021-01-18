package org.cheeryworks.liteql.jooq.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LiteQLJooqFlywayMigrationProperties.PREFIX)
public class LiteQLJooqFlywayMigrationProperties {

    public static final String PREFIX = LiteQLJooqProperties.PREFIX + ".migration";

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private boolean cleanBeforeMigration = false;

    public boolean isCleanBeforeMigration() {
        return cleanBeforeMigration;
    }

    public void setCleanBeforeMigration(boolean cleanBeforeMigration) {
        this.cleanBeforeMigration = cleanBeforeMigration;
    }

}
