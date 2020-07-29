package org.cheeryworks.liteql;

public class LiteQLProperties {

    private boolean enabled = true;

    private boolean migrationEnabled = false;

    private boolean diagnosticEnabled = false;

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

    public boolean isDiagnosticEnabled() {
        return diagnosticEnabled;
    }

    public void setDiagnosticEnabled(boolean diagnosticEnabled) {
        this.diagnosticEnabled = diagnosticEnabled;
    }

}
