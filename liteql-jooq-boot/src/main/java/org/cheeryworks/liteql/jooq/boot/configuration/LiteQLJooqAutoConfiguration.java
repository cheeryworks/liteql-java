package org.cheeryworks.liteql.jooq.boot.configuration;


import org.cheeryworks.liteql.jooq.service.query.JooqQueryExecutor;
import org.cheeryworks.liteql.jooq.service.query.JooqQueryParser;
import org.cheeryworks.liteql.jooq.service.query.JooqQueryService;
import org.cheeryworks.liteql.jooq.service.schema.migration.JooqMigrationService;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryEventPublisher;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryPublisher;
import org.cheeryworks.liteql.skeleton.event.publisher.schema.migration.MigrationEventPublisher;
import org.cheeryworks.liteql.skeleton.service.query.QueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.service.schema.migration.MigrationService;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.spring.boot.configuration.LiteQLAutoConfiguration;
import org.jooq.DSLContext;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        LiteQLJooqProperties.class
})
@ConditionalOnProperty(
        prefix = LiteQLJooqProperties.PREFIX,
        name = "enabled", matchIfMissing = true
)
@AutoConfigureBefore({LiteQLAutoConfiguration.class})
public class LiteQLJooqAutoConfiguration extends JooqAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(LiteQLJooqAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(Settings.class)
    public Settings jooqSettings() {
        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);
        settings.setRenderNameCase(RenderNameCase.LOWER);

        return settings;
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

}
