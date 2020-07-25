package org.cheeryworks.liteql.boot.configuration.jpa;


import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.jpa.JpaSchemaService;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EntityManager.class)
public class LiteQLJpaAutoConfiguration {

    @Bean
    public SqlCustomizer sqlCustomizer(LiteQLProperties liteQLProperties) {
        return new JpaSqlCustomizer(liteQLProperties);
    }

    @Bean
    public SchemaService jpaRepository(
            LiteQLProperties liteQLProperties, SqlCustomizer sqlCustomizer) {
        return new JpaSchemaService(liteQLProperties, sqlCustomizer);
    }

}
