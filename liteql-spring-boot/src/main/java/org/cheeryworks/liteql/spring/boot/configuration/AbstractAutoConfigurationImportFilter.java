package org.cheeryworks.liteql.spring.boot.configuration;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Set;

public abstract class AbstractAutoConfigurationImportFilter implements AutoConfigurationImportFilter {

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];

        Set<String> shouldSkippedAutoConfigurations = getShouldSkippedAutoConfigurations();

        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            matches[i] = !shouldSkippedAutoConfigurations.contains(autoConfigurationClasses[i]);
        }

        return matches;
    }

    protected abstract Set<String> getShouldSkippedAutoConfigurations();

}
