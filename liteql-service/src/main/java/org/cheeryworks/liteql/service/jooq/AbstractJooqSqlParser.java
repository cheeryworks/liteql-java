package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.model.enums.IndexType;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.DomainTypeName;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.type.index.AbstractIndex;
import org.cheeryworks.liteql.model.type.migration.operation.AbstractIndexMigrationOperation;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.jooq.util.JOOQDataTypeUtil;
import org.cheeryworks.liteql.service.jooq.util.JOOQDatabaseTypeUtil;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.util.StringEncoder;
import org.jooq.AlterTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.cheeryworks.liteql.service.type.SqlSchemaParser.INDEX_KEY_PREFIX;
import static org.cheeryworks.liteql.service.type.SqlSchemaParser.PRIMARY_KEY_PREFIX;
import static org.cheeryworks.liteql.service.type.SqlSchemaParser.UNIQUE_KEY_PREFIX;
import static org.jooq.impl.DSL.constraint;

public abstract class AbstractJooqSqlParser {

    private Repository repository;

    private Database database;

    private DSLContext dslContext;

    public AbstractJooqSqlParser(Repository repository, Database database) {
        this.repository = repository;
        this.database = database;

        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);
        settings.setRenderNameCase(RenderNameCase.LOWER);

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            settings.withRenderFormatted(true);
        }

        this.dslContext = new DefaultDSLContext(JOOQDatabaseTypeUtil.getSqlDialect(database), settings);
    }

    protected Repository getRepository() {
        return repository;
    }

    protected Database getDatabase() {
        return database;
    }

    protected DSLContext getDslContext() {
        return dslContext;
    }

    protected String parsingAddPrimaryKey(String tableName) {
        AlterTableFinalStep alterTableFinalStep = getDslContext()
                .alterTable(tableName)
                .add(constraint(PRIMARY_KEY_PREFIX + tableName).
                        primaryKey(DSL.field(Field.ID_FIELD_NAME)));

        return alterTableFinalStep.getSQL();
    }

    protected List<String> parsingIndexMigrationOperation(
            DomainTypeName domainTypeName, AbstractIndexMigrationOperation indexMigrationOperation) {
        String tableName = getTableName(domainTypeName.getFullname());

        List<String> sqls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(indexMigrationOperation.getIndexes())) {
            for (AbstractIndex index : indexMigrationOperation.getIndexes()) {
                String fieldsInString = Arrays.toString(
                        index.getFields().toArray(new String[index.getFields().size()]));
                String indexName = IndexType.Normal.equals(index.getType()) ? INDEX_KEY_PREFIX : UNIQUE_KEY_PREFIX
                        + StringEncoder.md5(tableName + "_" + fieldsInString).substring(0, 20);

                Query query;

                if (IndexType.Normal.equals(index.getType())) {
                    if (MigrationOperationType.DROP_INDEX.equals(indexMigrationOperation.getType())) {
                        query = getDslContext()
                                .dropIndex(indexName)
                                .on(tableName)
                                .cascade();
                    } else {
                        query = getDslContext()
                                .createIndex(indexName)
                                .on(tableName, index.getFields().toArray(new String[index.getFields().size()]));
                    }
                } else {
                    if (MigrationOperationType.DROP_UNIQUE.equals(indexMigrationOperation.getType())) {
                        query = getDslContext()
                                .alterTable(tableName)
                                .dropUnique(indexName)
                                .cascade();
                    } else {
                        query = getDslContext()
                                .alterTable(tableName)
                                .add(constraint(indexName).
                                        unique(index.getFields().toArray(
                                                new String[index.getFields().size()])));
                    }
                }

                sqls.add(query.getSQL(ParamType.INLINED));
            }
        }

        return sqls;
    }

    protected List<org.jooq.Field> getJooqFields(Set<Field> fields, Database database) {
        List<org.jooq.Field> jooqFields = new ArrayList<>();

        for (Field field : fields) {
            String fieldName = field.getName();

            if (field instanceof ReferenceField) {
                fieldName += "_" + Field.ID_FIELD_NAME;
            }

            jooqFields.add(DSL.field(fieldName, getJooqDataType(field, database)));
        }

        return jooqFields;
    }

    private DataType getJooqDataType(Field field, Database database) {
        if (field instanceof IdField) {
            return JOOQDataTypeUtil.getInstance(database).getStringDataType().length(128).nullable(false);
        } else if (field instanceof ReferenceField) {
            return JOOQDataTypeUtil.getInstance(database).getStringDataType()
                    .length(128)
                    .nullable(((ReferenceField) field).isNullable());
        } else if (field instanceof StringField) {
            StringField stringField = (StringField) field;
            DataType dataType = JOOQDataTypeUtil
                    .getInstance(database)
                    .getStringDataType()
                    .length(stringField.getLength())
                    .nullable(stringField.isNullable());

            return dataType;
        } else if (field instanceof IntegerField) {
            IntegerField integerField = (IntegerField) field;
            DataType dataType = JOOQDataTypeUtil
                    .getInstance(database)
                    .getIntegerDataType()
                    .nullable(integerField.isNullable());

            return dataType;
        }

        throw new IllegalArgumentException("Unsupported field type " + field.getClass().getSimpleName());
    }

    public static String getTableName(String domainTypeFullname) {
        return domainTypeFullname.replace(".", "_").toLowerCase();
    }

}
