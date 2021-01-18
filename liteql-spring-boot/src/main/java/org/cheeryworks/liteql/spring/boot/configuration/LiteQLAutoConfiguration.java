package org.cheeryworks.liteql.spring.boot.configuration;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.event.QueryEvent;
import org.cheeryworks.liteql.skeleton.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.skeleton.service.graphql.GraphQLService;
import org.cheeryworks.liteql.skeleton.service.query.DefaultQueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.DefaultQueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.QueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.service.sql.DefaultSqlCustomizer;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.spring.boot.configuration.jackson.LiteQLJacksonAutoConfiguration;
import org.cheeryworks.liteql.spring.boot.configuration.spring.security.web.LiteQLSecurityAutoConfiguration;
import org.cheeryworks.liteql.spring.event.publisher.query.SpringQueryEventPublisher;
import org.cheeryworks.liteql.spring.event.publisher.query.SpringQueryPublisher;
import org.cheeryworks.liteql.spring.event.publisher.schema.migration.SpringMigrationEventPublisher;
import org.cheeryworks.liteql.spring.json.graphql.GraphQLServiceController;
import org.cheeryworks.liteql.spring.json.query.LiteQLServiceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
        LiteQLJacksonAutoConfiguration.class,
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
    public GraphQLService graphQLService(
            SchemaService schemaService, QueryService queryService,
            QueryAccessDecisionService queryAccessDecisionService) {
        GraphQLService graphQLService = new DefaultGraphQLService(
                schemaService, queryService, queryAccessDecisionService);

        logger.info("GraphQLService is ready.");

        return graphQLService;
    }

}
