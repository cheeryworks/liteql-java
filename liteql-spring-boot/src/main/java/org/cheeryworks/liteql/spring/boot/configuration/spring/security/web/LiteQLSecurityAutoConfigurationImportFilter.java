package org.cheeryworks.liteql.spring.boot.configuration.spring.security.web;

import org.cheeryworks.liteql.spring.boot.configuration.AbstractAutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LiteQLSecurityAutoConfigurationImportFilter extends AbstractAutoConfigurationImportFilter {

    @Override
    protected Set<String> getShouldSkippedAutoConfigurations() {
        return new HashSet<>(
                Arrays.asList(
                        SecurityAutoConfiguration.class.getName()
                )
        );
    }

}
