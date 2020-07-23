package org.cheeryworks.liteql;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@ConfigurationProperties(prefix = LiteQLProperties.PREFIX)
public class LiteQLProperties {

    public static final String PREFIX = "liteql";

    private boolean enabled = true;

    private boolean migrationEnabled = false;

    private boolean diagnosticEnabled = false;

    private Set<String> packagesToScan = loadDefaultPackageToScan();

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

    public Set<String> getPackagesToScan() {
        return this.packagesToScan;
    }

    public void setPackagesToScan(Set<String> packagesToScan) {
        this.packagesToScan.addAll(packagesToScan);
    }

    private Set<String> loadDefaultPackageToScan() {
        try {
            PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                    = new PathMatchingResourcePatternResolver();

            Resource[] resources = pathMatchingResourcePatternResolver
                    .getResources("classpath*:META-INF/packages-to-scan-module.properties");
            Set<String> packagesToScan = new HashSet<>();
            if (resources != null) {
                for (Resource resource : resources) {
                    Properties properties = new Properties();
                    properties.load(resource.getInputStream());
                    packagesToScan.addAll(Arrays.asList(properties.get("packagesToScan").toString().split(",")));
                }
            }

            return packagesToScan;
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

}
