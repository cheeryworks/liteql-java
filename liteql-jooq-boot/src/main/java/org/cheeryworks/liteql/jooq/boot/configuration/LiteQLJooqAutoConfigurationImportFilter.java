package org.cheeryworks.liteql.jooq.boot.configuration;

import org.cheeryworks.liteql.spring.boot.configuration.AbstractAutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LiteQLJooqAutoConfigurationImportFilter extends AbstractAutoConfigurationImportFilter {

    @Override
    protected Set<String> getShouldSkippedAutoConfigurations() {
        return new HashSet<>(
                Arrays.asList(
                        JooqAutoConfiguration.class.getName(),
                        FlywayAutoConfiguration.class.getName()
                )
        );
    }

}
