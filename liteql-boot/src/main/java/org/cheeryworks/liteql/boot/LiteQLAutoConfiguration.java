package org.cheeryworks.liteql.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.service.graphql.GraphQLServiceController;
import org.cheeryworks.liteql.service.jooq.JooqSqlQueryService;
import org.cheeryworks.liteql.service.query.AuditingService;
import org.cheeryworks.liteql.service.query.DefaultAuditingService;
import org.cheeryworks.liteql.service.query.QueryServiceController;
import org.cheeryworks.liteql.service.repository.PathMatchingResourceRepository;
import org.cheeryworks.liteql.spring.JpaSqlCustomizer;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableConfigurationProperties(LiteQLProperties.class)
@ConditionalOnProperty(
        prefix = LiteQLProperties.PREFIX,
        name = "enabled", matchIfMissing = true
)
@AutoConfigureAfter({JooqAutoConfiguration.class})
@Import({
        QueryServiceController.class,
        GraphQLServiceController.class
})
public class LiteQLAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer liteqlJackson2ObjectMapperBuilderCustomizer() {
        return new LiteQLJackson2ObjectMapperBuilderCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean(Repository.class)
    public Repository repository(ObjectMapper objectMapper) {
        return new PathMatchingResourceRepository(objectMapper, "classpath*:/liteql");
    }

    @Bean
    public AuditingService auditingService() {
        return new DefaultAuditingService();
    }

    @Bean
    public QueryService queryService(
            Repository repository, ObjectMapper objectMapper, DSLContext dslContext,
            AuditingService auditingService, ApplicationEventPublisher applicationEventPublisher,
            EntityManagerFactory entityManagerFactory) {
        QueryService queryService = new JooqSqlQueryService(
                repository, objectMapper, dslContext,
                new JpaSqlCustomizer(entityManagerFactory), auditingService, applicationEventPublisher);

        return queryService;
    }

    @Bean
    public GraphQLService graphQLService(
            Repository repository, ObjectMapper objectMapper, QueryService queryService,
            LiteQLProperties liteQLProperties) {
        return new DefaultGraphQLService(
                repository, objectMapper, queryService,
                liteQLProperties.isAnnotationBasedGraphQLSchemaEnabled());
    }

}
