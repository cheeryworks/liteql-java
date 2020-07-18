package org.cheeryworks.liteql.boot.configuration.jackson;

import org.cheeryworks.liteql.jackson.LiteQLJackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class LiteQLJacksonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer liteQLJackson2ObjectMapperBuilderCustomizer() {
        return new LiteQLJackson2ObjectMapperBuilderCustomizer();
    }

}
