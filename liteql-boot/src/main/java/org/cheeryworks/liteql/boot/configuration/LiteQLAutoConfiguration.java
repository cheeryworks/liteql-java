package org.cheeryworks.liteql.boot.configuration;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.boot.configuration.jackson.LiteQLJacksonAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.jooq.LiteQLJooqAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.jpa.LiteQLJpaAutoConfiguration;
import org.cheeryworks.liteql.boot.configuration.spring.security.web.LiteQLSecurityAutoConfiguration;
import org.cheeryworks.liteql.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.service.graphql.GraphQLService;
import org.cheeryworks.liteql.service.graphql.json.GraphQLServiceController;
import org.cheeryworks.liteql.service.query.DefaultQueryAuditingService;
import org.cheeryworks.liteql.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.service.query.QueryAuditingService;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryExecutor;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryParser;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryService;
import org.cheeryworks.liteql.service.query.json.QueryServiceController;
import org.cheeryworks.liteql.service.query.sql.DefaultQueryAccessDecisionService;
import org.cheeryworks.liteql.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.schema.migration.MigrationService;
import org.cheeryworks.liteql.service.schema.migration.jooq.JooqMigrationService;
import org.cheeryworks.liteql.service.sql.DefaultSqlCustomizer;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.spring.context.SpringMigrationEventPublisher;
import org.cheeryworks.liteql.spring.context.SpringQueryEventPublisher;
import org.cheeryworks.liteql.util.LiteQL;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LiteQLSpringProperties.class)
@ConditionalOnProperty(
        prefix = LiteQLSpringProperties.PREFIX,
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
    @ConditionalOnMissingBean(SchemaService.class)
    public SchemaService schemaService(LiteQLProperties liteQLProperties) {
        return new DefaultSchemaService(
                liteQLProperties, "classpath*:/" + LiteQL.Constants.SCHEMA_DEFINITION_CLASSPATH_ROOT);
    }

    @Bean
    @ConditionalOnMissingBean(QueryAuditingService.class)
    public QueryAuditingService auditingService() {
        return new DefaultQueryAuditingService();
    }

    @Bean
    @ConditionalOnMissingBean(QueryAccessDecisionService.class)
    public QueryAccessDecisionService queryAccessDecisionService() {
        return new DefaultQueryAccessDecisionService();
    }

    @Bean
    @ConditionalOnMissingBean(SqlCustomizer.class)
    public SqlCustomizer sqlCustomizer(SchemaService schemaService) {
        return new DefaultSqlCustomizer(schemaService);
    }

    @Bean
    public JooqQueryParser jooqQueryParser(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService, SqlCustomizer sqlCustomizer,
            DSLContext dslContext) {
        return new JooqQueryParser(liteQLProperties, schemaService, sqlCustomizer, dslContext);

    }

    @Bean
    public JooqQueryExecutor jooqQueryExecutor(LiteQLProperties liteQLProperties, DSLContext dslContext) {
        return new JooqQueryExecutor(liteQLProperties, dslContext);
    }

    @Bean
    public MigrationService migrationService(
            LiteQLProperties liteQLProperties, JooqQueryParser jooqQueryParser,
            ApplicationEventPublisher applicationEventPublisher) {
        MigrationService migrationService = new JooqMigrationService(
                liteQLProperties, jooqQueryParser,
                new SpringMigrationEventPublisher(applicationEventPublisher));

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
            JooqQueryParser jooqQueryParser, JooqQueryExecutor jooqQueryExecutor,
            QueryAuditingService queryAuditingService, QueryAccessDecisionService queryAccessDecisionService,
            ApplicationEventPublisher applicationEventPublisher) {
        QueryService queryService = new JooqQueryService(
                liteQLProperties,
                jooqQueryParser, jooqQueryExecutor,
                queryAuditingService, queryAccessDecisionService,
                new SpringQueryEventPublisher(applicationEventPublisher));

        logger.info("QueryService is ready.");

        return queryService;
    }

    @Bean
    public GraphQLService graphQLService(SchemaService schemaService, QueryService queryService) {
        GraphQLService graphQLService = new DefaultGraphQLService(schemaService, queryService);

        logger.info("GraphQLService is ready.");

        return graphQLService;
    }

}
