package org.cheeryworks.liteql.boot.application;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LiteQLSpringApplication extends AbstractSpringApplication {

    private static final String[] DEFAULT_OPTIONAL_CONFIGURATION_PATHS = new String[]{
            "/etc/liteql/",
            "/etc/liteql/*/"
    };

    public LiteQLSpringApplication(Class<?> primarySource) {
        super(primarySource);
    }

    @Override
    protected Set<String> getDefaultOptionalConfigurationPaths() {
        return Arrays.stream(DEFAULT_OPTIONAL_CONFIGURATION_PATHS).collect(Collectors.toSet());
    }

}
