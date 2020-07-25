package org.cheeryworks.liteql.boot;

import org.cheeryworks.liteql.boot.application.AbstractApplication;
import org.cheeryworks.liteql.boot.configuration.jpa.LiteQLJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = LiteQLJpaAutoConfiguration.class)
public class TestApplication extends AbstractApplication {

    public static void main(String[] args) {
        run(TestApplication.class, args);
    }

}
