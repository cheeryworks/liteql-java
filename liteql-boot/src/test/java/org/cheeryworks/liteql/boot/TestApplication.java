package org.cheeryworks.liteql.boot;

import org.cheeryworks.liteql.boot.application.AbstractApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication(
        exclude = {
                FlywayAutoConfiguration.class
        }
)
public class TestApplication extends AbstractApplication {

    public static void main(String[] args) {
        run(TestApplication.class, args);
    }

}
