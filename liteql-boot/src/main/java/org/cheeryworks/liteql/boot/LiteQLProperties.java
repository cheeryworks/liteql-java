package org.cheeryworks.liteql.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LiteQLProperties.PREFIX)
public class LiteQLProperties {

    public static final String PREFIX = "liteql";

    private boolean enabled = true;

    private boolean annotationBasedGraphQLSchemaEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAnnotationBasedGraphQLSchemaEnabled() {
        return annotationBasedGraphQLSchemaEnabled;
    }

    public void setAnnotationBasedGraphQLSchemaEnabled(boolean annotationBasedGraphQLSchemaEnabled) {
        this.annotationBasedGraphQLSchemaEnabled = annotationBasedGraphQLSchemaEnabled;
    }

}
