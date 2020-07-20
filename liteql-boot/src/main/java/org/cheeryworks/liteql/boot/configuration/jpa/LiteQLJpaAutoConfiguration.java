package org.cheeryworks.liteql.boot.configuration.jpa;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.jpa.JpaRepository;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LiteQLJpaAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(LiteQLJpaAutoConfiguration.class);

    @Bean
    public SqlCustomizer sqlCustomizer() {
        return new JpaSqlCustomizer();
    }

    @Bean
    public Repository jpaRepository(ObjectMapper objectMapper, SqlCustomizer sqlCustomizer) {
        return new JpaRepository(objectMapper, sqlCustomizer);
    }

}
