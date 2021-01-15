package org.cheeryworks.liteql.spring.boot.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication extends AbstractApplication {

    public static void main(String[] args) {
        new LiteQLSpringApplication(TestApplication.class).run(args);
    }

}
