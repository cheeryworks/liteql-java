package org.cheeryworks.liteql.service.query;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.database.embedded.EmbeddedDatabaseFactory;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.util.JooqUtil;
import org.jooq.CreateTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertFinalStep;
import org.jooq.SQLDialect;
import org.jooq.SelectFinalStep;
import org.jooq.impl.DefaultDSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public abstract class AbstractQueryAccessDecisionService
        extends AbstractSqlService implements QueryAccessDecisionService {

    private static Logger logger = LoggerFactory.getLogger(AbstractQueryAccessDecisionService.class);

    private static final TimeBasedGenerator UUID_GENERATOR = Generators.timeBasedGenerator();

    public AbstractQueryAccessDecisionService(LiteQLProperties liteQLProperties, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, sqlCustomizer);
    }

    protected boolean decideToCreate(AbstractSaveQuery saveQuery, QueryConditions accessDecisionConditions) {
        int count = 0;

        String tableName = "T" + UUID_GENERATOR.generate().toString().replace("-", "");

        EmbeddedDatabase embeddedDatabase = null;
        DSLContext accessDecisionDslContext = null;

        EmbeddedDatabaseFactory embeddedDatabaseFactory = new EmbeddedDatabaseFactory();

        try {
            embeddedDatabase = embeddedDatabaseFactory.getDatabase();

            accessDecisionDslContext = new DefaultDSLContext(embeddedDatabase, SQLDialect.HSQLDB);

            List<Field> columns = new LinkedList<>();
            List<Object> values = new LinkedList<>();

            Map<String, Object> data = saveQuery.getData();

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                if (dataEntry.getValue() != null) {
                    columns.add(
                            field(
                                    dataEntry.getKey(),
                                    JooqUtil.getDataType(dataEntry.getValue().getClass())));

                    values.add(dataEntry.getValue());
                }
            }

            CreateTableFinalStep createTableFinalStep = accessDecisionDslContext
                    .createTable(table(tableName))
                    .columns(columns.toArray(new Field[columns.size()]));

            createTableFinalStep.execute();

            InsertFinalStep insertFinalStep = accessDecisionDslContext
                    .insertInto(table(tableName))
                    .columns(columns.toArray(new Field[columns.size()]))
                    .values(values.toArray());

            insertFinalStep.execute();

            SelectFinalStep selectFinalStep = accessDecisionDslContext
                    .selectCount()
                    .from(table(tableName))
                    .where(JooqUtil.getCondition(
                            saveQuery.getDomainTypeName(), accessDecisionConditions, null, null, getSqlCustomizer()));

            count = (int) selectFinalStep.fetchOne(0);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            embeddedDatabaseFactory.shutdownDatabase();
        }

        if (count == 1) {
            return true;
        }

        return false;
    }

}
