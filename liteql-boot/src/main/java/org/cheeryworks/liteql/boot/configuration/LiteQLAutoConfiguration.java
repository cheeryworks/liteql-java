package org.cheeryworks.liteql.boot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.boot.configuration.jackson.LiteQLJacksonAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.jooq.LiteQLJooqAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.jpa.LiteQLJpaAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.spring.security.web.LiteQLSecurityAutoConfiguration;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.cheeryworks.liteql.service.auditing.AuditingService;
import org.cheeryworks.liteql.service.auditing.DefaultAuditingService;
import org.cheeryworks.liteql.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.service.graphql.GraphQLService;
import org.cheeryworks.liteql.service.graphql.GraphQLServiceController;
import org.cheeryworks.liteql.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.query.jooq.JooqSqlQueryService;
import org.cheeryworks.liteql.service.query.json.QueryServiceController;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.schema.jooq.JooqSchemaService;
import org.cheeryworks.liteql.service.schema.migration.MigrationService;
import org.cheeryworks.liteql.service.schema.migration.jooq.JooqMigrationService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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

        LiteQLUtil.configureObjectMapper(builder);

        return builder.createXmlMapper(false).build();
    }

    @Bean
    @ConditionalOnMissingBean(SchemaService.class)
    public SchemaService repository(LiteQLProperties liteQLProperties, ObjectMapper objectMapper) {
        return new JooqSchemaService(liteQLProperties, objectMapper, "classpath*:/liteql");
    }

    @Bean
    public AuditingService auditingService() {
        return new DefaultAuditingService();
    }

    @Bean
    public MigrationService migrationService(
            LiteQLProperties liteQLProperties, SchemaService schemaService, ObjectProvider<DSLContext> dslContext,
            ObjectProvider<SqlCustomizer> sqlCustomizer) {
        MigrationService migrationService = new JooqMigrationService(
                liteQLProperties, schemaService, dslContext.getIfAvailable(), sqlCustomizer.getIfAvailable());

        logger.info("MigrationService is ready.");

        if (liteQLProperties.isMigrationEnabled()) {
            migrationService.migrate();
        } else {
            logger.info("Migration is disabled.");
        }

        return migrationService;
    }

    @Bean
    public QueryService queryService(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService, ObjectProvider<DSLContext> dslContext,
            ObjectProvider<SqlCustomizer> sqlCustomizer,
            AuditingService auditingService,
            ObjectProvider<QueryAccessDecisionService> queryAccessDecisionService,
            ApplicationEventPublisher applicationEventPublisher) {
        QueryService queryService = new JooqSqlQueryService(
                liteQLProperties, schemaService, dslContext.getIfAvailable(), sqlCustomizer.getIfAvailable(),
                auditingService, queryAccessDecisionService.getIfAvailable(), applicationEventPublisher);

        logger.info("QueryService is ready.");

        return queryService;
    }

    @Bean
    public GraphQLService graphQLService(
            LiteQLProperties liteQLProperties, SchemaService schemaService,
            ObjectMapper objectMapper, QueryService queryService) {
        GraphQLService graphQLService = new DefaultGraphQLService(
                liteQLProperties, schemaService, objectMapper, queryService);

        logger.info("GraphQLService is ready.");

        return graphQLService;
    }

}
