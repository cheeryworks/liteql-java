package org.cheeryworks.liteql.boot;

import org.cheeryworks.liteql.boot.application.AbstractApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication extends AbstractApplication {

    public static void main(String[] args) {
        run(TestApplication.class, args);
    }

}
