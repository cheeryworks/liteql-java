package org.cheeryworks.liteql.spring.boot.application;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.spring.event.SpringApplicationStartedEvent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.DEFAULT_CUSTOMIZED_CONFIGURATION_PATH;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.LITE_QL_PROFILE_KEY;

public abstract class AbstractSpringApplication extends SpringApplication {

    private static final String IMPORT_PROPERTY = "spring.config.import";

    private static final String[] CONFIGURATION_FILE_SUFFIX = new String[]{
            ".yml",
            ".yaml",
            ".properties"
    };

    public AbstractSpringApplication(Class<?> primarySource) {
        super(primarySource);
    }

    @Override
    public ConfigurableApplicationContext run(String[] args) {
        String customizedConfigurationPath = DEFAULT_CUSTOMIZED_CONFIGURATION_PATH;

        String profile = System.getProperty(LITE_QL_PROFILE_KEY);

        if (StringUtils.isNotBlank(profile)) {
            if (new File(DEFAULT_CUSTOMIZED_CONFIGURATION_PATH + File.separator + profile).exists()) {
                customizedConfigurationPath = DEFAULT_CUSTOMIZED_CONFIGURATION_PATH + File.separator + profile;
            } else {
                throw new IllegalArgumentException("Configuration of profile " + profile + " not found");
            }
        }

        customizedConfigurationPath =
                StringUtils.removeEnd(
                        org.springframework.util.StringUtils.cleanPath(customizedConfigurationPath), "/") + "/";

        List<String> optionalConfigurationPaths = new ArrayList<>();

        optionalConfigurationPaths.addAll(getDefaultOptionalConfigurationPaths());

        optionalConfigurationPaths.add(customizedConfigurationPath);

        List<String> optionalConfigurationFilePatterns = new ArrayList<>();

        optionalConfigurationPaths.stream().forEach(path -> {
            Arrays.stream(CONFIGURATION_FILE_SUFFIX).forEach(suffix -> {
                optionalConfigurationFilePatterns.add("optional:file:" + path + "application" + suffix);
            });
        });

        String optionalConfigurationFilePatternsInString
                = optionalConfigurationFilePatterns.stream().collect(Collectors.joining(","));

        String[] customizedArgs = new String[]{};
        int configLocationArgValueIndex = 0;
        boolean configLocationExist = false;

        for (int i = 0; i < args.length; i++) {
            String customizedArg = args[i];

            if (args[i].contains(IMPORT_PROPERTY)) {
                if (args[i].contains("=")) {
                    customizedArg = args[i] + "," + optionalConfigurationFilePatternsInString;
                } else {
                    configLocationArgValueIndex = i + 1;
                }

                configLocationExist = true;
            }

            if (configLocationArgValueIndex > 0 && configLocationArgValueIndex == i) {
                customizedArg = args[i] + "," + optionalConfigurationFilePatternsInString;
            }

            customizedArgs = ArrayUtils.add(customizedArgs, customizedArg);
        }

        if (!configLocationExist) {
            customizedArgs = ArrayUtils.add(
                    customizedArgs,
                    "--" + IMPORT_PROPERTY + "=" + optionalConfigurationFilePatternsInString);
        }

        return super.run(customizedArgs);
    }

    protected abstract Set<String> getDefaultOptionalConfigurationPaths();

    @Override
    protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {

        context.publishEvent(new SpringApplicationStartedEvent(context));

    }

}
