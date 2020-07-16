package org.cheeryworks.liteql.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.jooq.LiteQLJooqAutoConfiguration;
import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.MigrationService;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.service.graphql.GraphQLServiceController;
import org.cheeryworks.liteql.service.jooq.JooqSqlMigrationService;
import org.cheeryworks.liteql.service.jooq.JooqSqlQueryService;
import org.cheeryworks.liteql.service.query.AuditingService;
import org.cheeryworks.liteql.service.query.DefaultAuditingService;
import org.cheeryworks.liteql.service.query.QueryServiceController;
import org.cheeryworks.liteql.service.repository.PathMatchingResourceRepository;
import org.cheeryworks.liteql.spring.LiteQLSpringJpaAutoConfiguration;
import org.cheeryworks.liteql.spring.LiteQLSpringSecurityAutoConfiguration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LiteQLProperties.class)
@ConditionalOnProperty(
        prefix = LiteQLProperties.PREFIX,
        name = "enabled", matchIfMissing = true
)
@AutoConfigureAfter({
        LiteQLJooqAutoConfiguration.class,
        LiteQLSpringJpaAutoConfiguration.class,
        LiteQLSpringSecurityAutoConfiguration.class
})
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
    public MigrationService migrationService(
            Repository repository, DSLContext dslContext,
            ObjectProvider<SqlCustomizer> sqlCustomizer,
            LiteQLProperties properties) {
        MigrationService migrationService
                = new JooqSqlMigrationService(repository, dslContext, sqlCustomizer.getIfAvailable());

        if (properties.isMigrationEnabled()) {
            migrationService.migrate();
        }

        return migrationService;
    }

    @Bean
    public QueryService queryService(
            Repository repository, ObjectMapper objectMapper, DSLContext dslContext,
            ObjectProvider<SqlCustomizer> sqlCustomizer,
            AuditingService auditingService, ApplicationEventPublisher applicationEventPublisher) {
        QueryService queryService = new JooqSqlQueryService(
                repository, objectMapper, dslContext, sqlCustomizer.getIfAvailable(),
                auditingService, applicationEventPublisher);

        return queryService;
    }

    @Bean
    public GraphQLService graphQLService(
            Repository repository, ObjectMapper objectMapper, QueryService queryService,
            LiteQLProperties liteQLProperties) {
        return new DefaultGraphQLService(
                repository, objectMapper, queryService,
                liteQLProperties.isGraphQLSchemaEnabled(),
                liteQLProperties.isAnnotationBasedGraphQLSchemaEnabled());
    }

}
