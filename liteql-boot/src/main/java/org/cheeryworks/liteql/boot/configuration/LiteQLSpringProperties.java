package org.cheeryworks.liteql.boot.configuration;

import org.cheeryworks.liteql.LiteQLProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@ConfigurationProperties(prefix = LiteQLSpringProperties.PREFIX)
public class LiteQLSpringProperties extends LiteQLProperties {

    public static final String PREFIX = "liteql";

    private Set<String> packagesToScan = loadDefaultPackageToScan();

    @Override
    public Set<String> getPackagesToScan() {
        return this.packagesToScan;
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
