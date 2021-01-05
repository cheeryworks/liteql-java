package org.cheeryworks.liteql.boot.application;

import org.cheeryworks.liteql.event.ApplicationStartedEvent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class LiteQLSpringApplication extends SpringApplication {

    public LiteQLSpringApplication(Class<?>... primarySources) {
        super(primarySources);
    }

    @Override
    protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {

        context.publishEvent(new ApplicationStartedEvent(context));

    }

}
