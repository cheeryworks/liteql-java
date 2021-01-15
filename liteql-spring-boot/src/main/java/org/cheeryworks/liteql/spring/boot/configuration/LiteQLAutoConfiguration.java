package org.cheeryworks.liteql.spring.boot.configuration;

import org.cheeryworks.liteql.jooq.service.query.JooqQueryExecutor;
import org.cheeryworks.liteql.jooq.service.query.JooqQueryParser;
import org.cheeryworks.liteql.jooq.service.query.JooqQueryService;
import org.cheeryworks.liteql.jooq.service.schema.migration.JooqMigrationService;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.publisher.QueryEventPublisher;
import org.cheeryworks.liteql.skeleton.query.event.publisher.QueryPublisher;
import org.cheeryworks.liteql.skeleton.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.skeleton.service.graphql.GraphQLService;
import org.cheeryworks.liteql.skeleton.service.query.DefaultQueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.QueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.query.sql.DefaultQueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.service.schema.migration.MigrationEventPublisher;
import org.cheeryworks.liteql.skeleton.service.schema.migration.MigrationService;
import org.cheeryworks.liteql.skeleton.service.sql.DefaultSqlCustomizer;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.spring.boot.configuration.jackson.LiteQLJacksonAutoConfiguration;
import org.cheeryworks.liteql.spring.boot.configuration.jooq.LiteQLJooqAutoConfiguration;
import org.cheeryworks.liteql.spring.boot.configuration.jpa.LiteQLJpaAutoConfiguration;
import org.cheeryworks.liteql.spring.boot.configuration.spring.security.web.LiteQLSecurityAutoConfiguration;
import org.cheeryworks.liteql.spring.context.SpringMigrationEventPublisher;
import org.cheeryworks.liteql.spring.context.SpringQueryEventPublisher;
import org.cheeryworks.liteql.spring.context.SpringQueryPublisher;
import org.cheeryworks.liteql.spring.json.graphql.GraphQLServiceController;
import org.cheeryworks.liteql.spring.json.query.LiteQLServiceController;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
        LiteQLServiceController.class,
        GraphQLServiceController.class
})
public class LiteQLAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(LiteQLAutoConfiguration.class);

    private ApplicationEventPublisher applicationEventPublisher;

    private Sinks.Many<PublicQuery> querySinksMany;

    private Sinks.Many<QueryEvent> queryEventSinksMany;

    public LiteQLAutoConfiguration(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.querySinksMany = Sinks.many().unicast().onBackpressureBuffer();
        this.queryEventSinksMany = Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public SpringQueryPublisher springQueryPublisher(
            LiteQLProperties liteQLProperties, ApplicationEventPublisher applicationEventPublisher) {
        return new SpringQueryPublisher(liteQLProperties, applicationEventPublisher, querySinksMany);
    }

    @Bean
    public Supplier<Flux<PublicQuery>> querySupplier() {
        return () -> this.querySinksMany.asFlux();
    }

    @Bean
    public SpringQueryEventPublisher springQueryEventPublisher(
            LiteQLProperties liteQLProperties, ApplicationEventPublisher applicationEventPublisher) {
        return new SpringQueryEventPublisher(liteQLProperties, applicationEventPublisher, queryEventSinksMany);
    }

    @Bean
    public Supplier<Flux<QueryEvent>> queryEventSupplier() {
        return () -> this.queryEventSinksMany.asFlux();
    }

    @Bean
    public SpringMigrationEventPublisher springMigrationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        return new SpringMigrationEventPublisher(applicationEventPublisher);
    }

    @Bean
    public Consumer<PublicQuery> queryConsumer() {
        return publicQuery -> {
            this.applicationEventPublisher.publishEvent(publicQuery);
        };
    }

    @Bean
    public Consumer<QueryEvent> queryEventConsumer() {
        return queryEvent -> {
            this.applicationEventPublisher.publishEvent(queryEvent);
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchemaService.class)
    public SchemaService schemaService(LiteQLProperties liteQLProperties) {
        return new DefaultSchemaService(liteQLProperties);
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
            MigrationEventPublisher migrationEventPublisher) {
        MigrationService migrationService = new JooqMigrationService(
                liteQLProperties, jooqQueryParser, migrationEventPublisher);

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
            QueryAuditingService queryAuditingService,
            QueryPublisher queryPublisher, QueryEventPublisher queryEventPublisher) {
        QueryService queryService = new JooqQueryService(
                liteQLProperties,
                jooqQueryParser, jooqQueryExecutor,
                queryAuditingService,
                queryPublisher, queryEventPublisher);

        logger.info("QueryService is ready.");

        return queryService;
    }

    @Bean
    public GraphQLService graphQLService(
            SchemaService schemaService, QueryService queryService,
            QueryAccessDecisionService queryAccessDecisionService) {
        GraphQLService graphQLService = new DefaultGraphQLService(
                schemaService, queryService, queryAccessDecisionService);

        logger.info("GraphQLService is ready.");

        return graphQLService;
    }

}
