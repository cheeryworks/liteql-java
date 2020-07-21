package org.cheeryworks.liteql.boot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.boot.configuration.jackson.LiteQLJacksonAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.jooq.LiteQLJooqAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.jpa.LiteQLJpaAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.spring.security.web.LiteQLSecurityAutoConfiguration;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.MigrationService;
import org.cheeryworks.liteql.service.QueryConditionNormalizer;
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
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LiteQLProperties.class)
@ConditionalOnProperty(
        prefix = LiteQLProperties.PREFIX,
        name = "enabled", matchIfMissing = true
)
@AutoConfigureAfter({
        JacksonAutoConfiguration.class,
        JooqAutoConfiguration.class,
        LiteQLJacksonAutoConfiguration.class,
        LiteQLJooqAutoConfiguration.class,
        LiteQLJpaAutoConfiguration.class,
        LiteQLSecurityAutoConfiguration.class
})
@ConditionalOnBean(DSLContext.class)
@Import({
        QueryServiceController.class,
        GraphQLServiceController.class
})
public class LiteQLAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(LiteQLAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        LiteQLJsonUtil.configureObjectMapper(builder);

        return builder.createXmlMapper(false).build();
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

        logger.info("MigrationService is ready.");

        if (properties.isMigrationEnabled()) {
            migrationService.migrate();
        } else {
            logger.info("Migration is disabled.");
        }

        return migrationService;
    }

    @Bean
    public QueryService queryService(
            Repository repository, DSLContext dslContext,
            ObjectProvider<SqlCustomizer> sqlCustomizer,
            AuditingService auditingService, ApplicationEventPublisher applicationEventPublisher,
            ObjectProvider<List<QueryConditionNormalizer>> queryConditionNormalizers) {
        QueryService queryService = new JooqSqlQueryService(
                repository, dslContext, sqlCustomizer.getIfAvailable(),
                auditingService, applicationEventPublisher, queryConditionNormalizers.getIfAvailable());

        logger.info("QueryService is ready.");

        return queryService;
    }

    @Bean
    public GraphQLService graphQLService(
            Repository repository, ObjectMapper objectMapper, QueryService queryService) {
        GraphQLService graphQLService = new DefaultGraphQLService(repository, objectMapper, queryService);

        logger.info("GraphQLService is ready.");

        return graphQLService;
    }

}
