package org.cheeryworks.liteql.spring.boot.configuration.jpa;


import org.cheeryworks.liteql.jpa.JpaSchemaService;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.spring.boot.configuration.LiteQLSpringProperties;
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
    public SqlCustomizer sqlCustomizer(SchemaService schemaService) {
        return new JpaSqlCustomizer(schemaService);
    }

    @Bean
    public SchemaService jpaRepository(LiteQLSpringProperties liteQLSpringProperties) {
        return new JpaSchemaService(liteQLSpringProperties);
    }

}
