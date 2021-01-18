package org.cheeryworks.liteql.spring.boot.configuration.jackson;

import org.cheeryworks.liteql.spring.boot.configuration.AbstractAutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LiteQLJacksonAutoConfigurationImportFilter extends AbstractAutoConfigurationImportFilter {

    @Override
    protected Set<String> getShouldSkippedAutoConfigurations() {
        return new HashSet<>(
                Arrays.asList(
                        JacksonAutoConfiguration.class.getName()
                )
        );
    }

}
