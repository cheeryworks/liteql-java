package org.cheeryworks.liteql.boot.configuration.jpa;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.jpa.JpaRepository;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EntityManager.class)
public class LiteQLJpaAutoConfiguration {

    @Bean
    public SqlCustomizer sqlCustomizer() {
        return new JpaSqlCustomizer();
    }

    @Bean
    public Repository jpaRepository(ObjectMapper objectMapper, SqlCustomizer sqlCustomizer) {
        return new JpaRepository(objectMapper, sqlCustomizer);
    }

}
