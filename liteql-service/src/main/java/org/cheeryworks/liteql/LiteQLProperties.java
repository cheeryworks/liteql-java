package org.cheeryworks.liteql;

public class LiteQLProperties {

    private boolean enabled = true;

    private boolean jsonBasedSchemaEnabled = true;

    private boolean migrationEnabled = true;

    private boolean diagnosticEnabled = false;

    private String dataPath = "/tmp/liteql";

    private boolean messagingEnabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isJsonBasedSchemaEnabled() {
        return jsonBasedSchemaEnabled;
    }

    public void setJsonBasedSchemaEnabled(boolean jsonBasedSchemaEnabled) {
        this.jsonBasedSchemaEnabled = jsonBasedSchemaEnabled;
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

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public boolean isMessagingEnabled() {
        return messagingEnabled;
    }

    public void setMessagingEnabled(boolean messagingEnabled) {
        this.messagingEnabled = messagingEnabled;
    }

}
