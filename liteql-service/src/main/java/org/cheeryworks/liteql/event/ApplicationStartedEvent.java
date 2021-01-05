package org.cheeryworks.liteql.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class ApplicationStartedEvent extends ApplicationEvent {

    public ApplicationStartedEvent(ConfigurableApplicationContext context) {
        super(context);
    }

}
