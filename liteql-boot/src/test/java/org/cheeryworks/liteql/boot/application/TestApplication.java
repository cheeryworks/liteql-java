package org.cheeryworks.liteql.boot.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication extends AbstractApplication {

    public static void main(String[] args) {
        run(TestApplication.class, args);
    }

}
