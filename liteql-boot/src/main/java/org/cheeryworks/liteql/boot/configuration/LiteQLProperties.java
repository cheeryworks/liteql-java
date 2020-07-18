package org.cheeryworks.liteql.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LiteQLProperties.PREFIX)
public class LiteQLProperties {

    public static final String PREFIX = "liteql";

    private boolean enabled = true;

    private boolean migrationEnabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMigrationEnabled() {
        return migrationEnabled;
    }

    public void setMigrationEnabled(boolean migrationEnabled) {
        this.migrationEnabled = migrationEnabled;
    }

}
