package org.cheeryworks.liteql.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.boot.LiteQLAutoConfiguration;
import org.cheeryworks.liteql.service.Repository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(EntityManagerFactory.class)
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
@AutoConfigureBefore(LiteQLAutoConfiguration.class)
@Import({
        JpaSchemaServiceController.class,
})
public class LiteQLSpringJpaAutoConfiguration {

    @Bean
    public JpaSchemaService jpaSchemaService(EntityManagerFactory entityManagerFactory, ObjectMapper objectMapper) {
        return new DefaultJpaSchemaService(entityManagerFactory, objectMapper);
    }

    @Bean
    public Repository jpaRepository(ObjectMapper objectMapper, JpaSchemaService jpaSchemaService) {
        return new JpaRepository(objectMapper, jpaSchemaService, "classpath*:/liteql");
    }

}
