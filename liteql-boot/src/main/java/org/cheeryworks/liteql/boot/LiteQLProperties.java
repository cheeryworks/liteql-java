package org.cheeryworks.liteql.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LiteQLProperties.PREFIX)
public class LiteQLProperties {

    public static final String PREFIX = "liteql";

    private boolean enabled = true;

    private boolean graphQLSchemaEnabled = false;

    private boolean annotationBasedGraphQLSchemaEnabled = true;

    private boolean migrationEnabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isGraphQLSchemaEnabled() {
        return graphQLSchemaEnabled;
    }

    public void setGraphQLSchemaEnabled(boolean graphQLSchemaEnabled) {
        this.graphQLSchemaEnabled = graphQLSchemaEnabled;
    }

    public boolean isAnnotationBasedGraphQLSchemaEnabled() {
        return annotationBasedGraphQLSchemaEnabled;
    }

    public void setAnnotationBasedGraphQLSchemaEnabled(boolean annotationBasedGraphQLSchemaEnabled) {
        this.annotationBasedGraphQLSchemaEnabled = annotationBasedGraphQLSchemaEnabled;
    }

    public boolean isMigrationEnabled() {
        return migrationEnabled;
    }

    public void setMigrationEnabled(boolean migrationEnabled) {
        this.migrationEnabled = migrationEnabled;
    }

}
