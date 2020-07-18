package org.cheeryworks.liteql.boot.configuration.jpa;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.jpa.JpaRepository;
import org.cheeryworks.liteql.jpa.JpaSqlCustomizer;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({
        ObjectMapper.class,
        EntityManagerFactory.class
})
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
public class LiteQLJpaAutoConfiguration {

    @Bean
    public SqlCustomizer sqlCustomizer(EntityManagerFactory entityManagerFactory) {
        return new JpaSqlCustomizer(entityManagerFactory);
    }

    @Bean
    public Repository jpaRepository(ObjectMapper objectMapper, EntityManagerFactory entityManagerFactory) {
        return new JpaRepository(objectMapper, entityManagerFactory);
    }

}
