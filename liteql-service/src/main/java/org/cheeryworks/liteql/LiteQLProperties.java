package org.cheeryworks.liteql;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class LiteQLProperties {

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

    private Set<String> loadDefaultPackageToScan() {
        try {
            Enumeration<URL> urlEnumeration
                    = getClass().getClassLoader().getResources("META-INF/packages-to-scan-module.properties");

            while (urlEnumeration.hasMoreElements()) {
                System.out.println(urlEnumeration.nextElement().getPath());
            }

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
