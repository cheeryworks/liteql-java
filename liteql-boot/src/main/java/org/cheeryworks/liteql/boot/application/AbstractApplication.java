package org.cheeryworks.liteql.boot.application;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.util.Collections;

import static org.cheeryworks.liteql.util.LiteQL.Constants.DEFAULT_CUSTOMIZED_CONFIGURATION_PATH;
import static org.cheeryworks.liteql.util.LiteQL.Constants.LITEQL_PROFILE_KEY;

public abstract class AbstractApplication extends SpringBootServletInitializer {

    private static final String IMPORT_PROPERTY = "spring.config.import";

    private static final String DEFAULT_OPTIONAL_IMPORTS
            = "optional:file:/etc/soupe/application.yml,"
            + "optional:file:/etc/soupe/*/application.yml,"
            + "optional:file:/etc/soupe/application.yaml,"
            + "optional:file:/etc/soupe/*/application.yaml,"
            + "optional:file:/etc/soupe/application.properties,"
            + "optional:file:/etc/soupe/*/application.properties";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));

        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setHttpOnly(true);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(getClass());
    }

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionListener> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    protected static void run(Class<?> primarySource, String[] args) {
        String customizedConfigurationPath = DEFAULT_CUSTOMIZED_CONFIGURATION_PATH;

        String profile = System.getProperty(LITEQL_PROFILE_KEY);

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

        customizedConfigurationPath = "optional:file:" + customizedConfigurationPath + "application.yml";

        customizedConfigurationPath += "," + DEFAULT_OPTIONAL_IMPORTS;

        String[] customizedArgs = new String[]{};
        int configLocationArgValueIndex = 0;
        boolean configLocationExist = false;

        for (int i = 0; i < args.length; i++) {
            String customizedArg = args[i];

            if (args[i].contains(IMPORT_PROPERTY)) {
                if (args[i].contains("=")) {
                    customizedArg = args[i] + "," + customizedConfigurationPath;
                } else {
                    configLocationArgValueIndex = i + 1;
                }

                configLocationExist = true;
            }

            if (configLocationArgValueIndex > 0 && configLocationArgValueIndex == i) {
                customizedArg = args[i] + "," + customizedConfigurationPath;
            }

            customizedArgs = ArrayUtils.add(customizedArgs, customizedArg);
        }

        if (!configLocationExist) {
            customizedArgs = ArrayUtils.add(
                    customizedArgs,
                    "--" + IMPORT_PROPERTY + "=" + customizedConfigurationPath);
        }

        SpringApplication.run(primarySource, customizedArgs);
    }

}
