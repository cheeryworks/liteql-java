package org.cheeryworks.liteql.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringApplicationStartedEvent extends ApplicationEvent {

    public SpringApplicationStartedEvent(ConfigurableApplicationContext context) {
        super(context);
    }

}
