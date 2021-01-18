package org.cheeryworks.liteql.spring.boot.configuration.jackson;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LiteQLJacksonAutoConfiguration extends JacksonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer liteQLJackson2ObjectMapperBuilderCustomizer() {
        return new LiteQLJackson2ObjectMapperBuilderCustomizer();
    }

}
