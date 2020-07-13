package org.cheeryworks.liteql.model.util;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class LiteQLConstants {

    public static final String SPECIFICATION_VERSION = "1.0.0";

    public static final String NAMESPACE = "liteql";

    public static final String WORD_CONCAT = "_";

    public static final String NAME_CONCAT = ".";

    public static final String DEFAULT_CUSTOMIZED_CONFIGURATION_PATH
            = System.getProperty("user.home") + File.separator + ".liteql";

    public static final String PLATFORM_VERSION_SPECIFIED_CUSTOMIZED_CONFIGURATION_PATH
            = DEFAULT_CUSTOMIZED_CONFIGURATION_PATH + File.separator + SPECIFICATION_VERSION;

    public static final String LITEQL_PROFILE_KEY = "liteql.profile";

    public static final String DIAGNOSTIC_ENABLED_KEY = "diagnostic.enabled";

    public static final boolean DIAGNOSTIC_ENABLED;

    static {
        DIAGNOSTIC_ENABLED = BooleanUtils.toBoolean(System.getProperty(DIAGNOSTIC_ENABLED_KEY));
    }

    private static Set<String> packagesToScan;

    static {
        packagesToScan = loadPackageToScan();
    }

    public static Set<String> getPackageToScan() {
        return packagesToScan;
    }

    private static Set<String> loadPackageToScan() {
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
