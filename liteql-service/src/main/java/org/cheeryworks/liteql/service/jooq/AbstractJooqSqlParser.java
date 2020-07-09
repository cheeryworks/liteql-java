package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.model.enums.IndexType;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.BlobField;
import org.cheeryworks.liteql.model.type.field.ClobField;
import org.cheeryworks.liteql.model.type.field.DecimalField;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.type.field.TimestampField;
import org.cheeryworks.liteql.model.type.index.AbstractIndex;
import org.cheeryworks.liteql.model.type.migration.operation.AbstractIndexMigrationOperation;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.cheeryworks.liteql.service.AbstractSqlParser;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.jooq.util.JOOQDataTypeUtil;
import org.cheeryworks.liteql.service.jooq.util.JOOQDatabaseTypeUtil;
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

import static org.cheeryworks.liteql.service.schema.SqlSchemaParser.INDEX_KEY_PREFIX;
import static org.cheeryworks.liteql.service.schema.SqlSchemaParser.PRIMARY_KEY_PREFIX;
import static org.cheeryworks.liteql.service.schema.SqlSchemaParser.UNIQUE_KEY_PREFIX;
import static org.jooq.impl.DSL.constraint;

public abstract class AbstractJooqSqlParser extends AbstractSqlParser {

    private Repository repository;

    private Database database;

    private DSLContext dslContext;

    private SqlCustomizer sqlCustomizer;

    public AbstractJooqSqlParser(Repository repository, Database database, SqlCustomizer sqlCustomizer) {
        this.repository = repository;
        this.database = database;
        this.sqlCustomizer = sqlCustomizer;

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
                        primaryKey(DSL.field(IdField.ID_FIELD_NAME)));

        return alterTableFinalStep.getSQL();
    }

    protected List<String> parsingIndexMigrationOperation(
            TypeName domainTypeName, AbstractIndexMigrationOperation indexMigrationOperation) {
        String tableName = getTableName(domainTypeName);

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
            String fieldName = StringUtil.camelNameToLowerDashConnectedLowercaseName(field.getName());

            if (field instanceof ReferenceField) {
                fieldName += "_" + IdField.ID_FIELD_NAME;
            }

            jooqFields.add(DSL.field(fieldName, getJooqDataType(field, database)));
        }

        return jooqFields;
    }

    private DataType getJooqDataType(Field field, Database database) {
        switch (field.getType()) {
            case String:
                if (IdField.ID_FIELD_NAME.equalsIgnoreCase(field.getName())) {
                    IdField idField = (IdField) field;

                    return JOOQDataTypeUtil
                            .getInstance(database)
                            .getStringDataType()
                            .length(idField.getLength())
                            .nullable(idField.isNullable());
                } else {
                    StringField stringField = (StringField) field;

                    return JOOQDataTypeUtil
                            .getInstance(database)
                            .getStringDataType()
                            .length(stringField.getLength())
                            .nullable(stringField.isNullable());
                }
            case Integer:
                IntegerField integerField = (IntegerField) field;

                return JOOQDataTypeUtil
                        .getInstance(database)
                        .getIntegerDataType(integerField.isNullable());
            case Timestamp:
                TimestampField timestampField = (TimestampField) field;

                return JOOQDataTypeUtil
                        .getInstance(database)
                        .getTimestampDataType(timestampField.isNullable());
            case Boolean:
                return JOOQDataTypeUtil
                        .getInstance(database)
                        .getBooleanDataType();
            case Decimal:
                DecimalField decimalField = (DecimalField) field;

                return JOOQDataTypeUtil
                        .getInstance(database)
                        .getBigDecimalDataType(decimalField.isNullable());
            case Clob:
                ClobField clobField = (ClobField) field;
                return JOOQDataTypeUtil
                        .getInstance(database)
                        .getClobDataType(clobField.isNullable());
            case Blob:
                BlobField blobField = (BlobField) field;
                return JOOQDataTypeUtil
                        .getInstance(database)
                        .getBlobDataType(blobField.isNullable());
            case Reference:
                return JOOQDataTypeUtil.getInstance(database).getStringDataType()
                        .length(128)
                        .nullable(((ReferenceField) field).isNullable());
            default:
                throw new IllegalArgumentException("Unsupported field type " + field.getClass().getSimpleName());
        }
    }

    @Override
    public String getTableName(TypeName domainTypeName) {
        if (sqlCustomizer != null) {
            return sqlCustomizer.getTableName(domainTypeName);
        }

        return domainTypeName.getFullname().replace(".", "_").toLowerCase();
    }

}
