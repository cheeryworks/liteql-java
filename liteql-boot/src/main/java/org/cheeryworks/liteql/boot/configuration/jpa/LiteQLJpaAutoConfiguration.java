package org.cheeryworks.liteql.boot.configuration.jpa;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.jpa.JpaSchemaService;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EntityManager.class)
public class LiteQLJpaAutoConfiguration {

    @Bean
    public SqlCustomizer sqlCustomizer(ObjectProvider<LiteQLProperties> liteQLProperties) {
        return new JpaSqlCustomizer(liteQLProperties.getIfAvailable());
    }

    @Bean
    public SchemaService jpaRepository(
            ObjectProvider<LiteQLProperties> liteQLProperties,
            ObjectProvider<ObjectMapper> objectMapper, SqlCustomizer sqlCustomizer) {
        return new JpaSchemaService(
                liteQLProperties.getIfAvailable(), objectMapper.getIfAvailable(), sqlCustomizer);
    }

}
