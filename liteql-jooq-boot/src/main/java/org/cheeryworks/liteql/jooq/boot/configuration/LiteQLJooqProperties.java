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

    private boolean migrationEnabled = true;

    private boolean cleanBeforeMigration = false;

    public boolean isMigrationEnabled() {
        return migrationEnabled;
    }

    public void setMigrationEnabled(boolean migrationEnabled) {
        this.migrationEnabled = migrationEnabled;
    }

    public boolean isCleanBeforeMigration() {
        return cleanBeforeMigration;
    }

    public void setCleanBeforeMigration(boolean cleanBeforeMigration) {
        this.cleanBeforeMigration = cleanBeforeMigration;
    }

}
