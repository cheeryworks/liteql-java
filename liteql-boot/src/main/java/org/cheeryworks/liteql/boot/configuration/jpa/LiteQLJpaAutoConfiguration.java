package org.cheeryworks.liteql.boot.configuration.jpa;


import org.cheeryworks.liteql.boot.configuration.LiteQLSpringProperties;
import org.cheeryworks.liteql.jpa.JpaSchemaService;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LiteQLSpringProperties.class)
@ConditionalOnClass(EntityManager.class)
public class LiteQLJpaAutoConfiguration {

    @Bean
    public SqlCustomizer sqlCustomizer() {
        return new JpaSqlCustomizer();
    }

    @Bean
    public SchemaService jpaRepository(
            LiteQLSpringProperties liteQLSpringProperties, SqlCustomizer sqlCustomizer) {
        return new JpaSchemaService(liteQLSpringProperties, sqlCustomizer);
    }

}
