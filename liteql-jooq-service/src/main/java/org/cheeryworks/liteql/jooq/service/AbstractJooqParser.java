package org.cheeryworks.liteql.jooq.service;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.jooq.util.JooqUtil;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.enums.IndexType;
import org.cheeryworks.liteql.skeleton.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.skeleton.schema.field.BlobField;
import org.cheeryworks.liteql.skeleton.schema.field.ClobField;
import org.cheeryworks.liteql.skeleton.schema.field.DecimalField;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;
import org.cheeryworks.liteql.skeleton.schema.field.IntegerField;
import org.cheeryworks.liteql.skeleton.schema.field.LongField;
import org.cheeryworks.liteql.skeleton.schema.field.ReferenceField;
import org.cheeryworks.liteql.skeleton.schema.field.StringField;
import org.cheeryworks.liteql.skeleton.schema.field.TimestampField;
import org.cheeryworks.liteql.skeleton.schema.index.AbstractIndexDefinition;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.AbstractIndexMigrationOperation;
import org.cheeryworks.liteql.skeleton.service.query.sql.AbstractSqlParser;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.skeleton.util.StringEncoder;
import org.jooq.AlterTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.cheeryworks.liteql.skeleton.util.DatabaseUtil.INDEX_KEY_PREFIX;
import static org.cheeryworks.liteql.skeleton.util.DatabaseUtil.PRIMARY_KEY_PREFIX;
import static org.cheeryworks.liteql.skeleton.util.DatabaseUtil.UNIQUE_KEY_PREFIX;
import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.table;

public abstract class AbstractJooqParser extends AbstractSqlParser {

    private DSLContext dslContext;

    public AbstractJooqParser(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService, SqlCustomizer sqlCustomizer,
            DSLContext dslContext) {
        super(liteQLProperties, schemaService, sqlCustomizer);

        this.dslContext = dslContext;
    }

    public DSLContext getDslContext() {
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
            TypeName domainTypeName,
            AbstractIndexMigrationOperation<? extends AbstractIndexDefinition> indexMigrationOperation) {
        String tableName = getSqlCustomizer().getTableName(domainTypeName);

        DomainTypeDefinition domainTypeDefinition = getSchemaService().getDomainTypeDefinition(domainTypeName);

        List<String> sqls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(indexMigrationOperation.getIndexes())) {
            for (AbstractIndexDefinition index : indexMigrationOperation.getIndexes()) {
                String fieldsInString = Arrays.toString(
                        index.getFields().toArray(new String[index.getFields().size()]));
                String indexName = (IndexType.Normal.equals(index.getType()) ? INDEX_KEY_PREFIX : UNIQUE_KEY_PREFIX)
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
                                .on(table(tableName), transformToJooqFields(domainTypeDefinition, index.getFields()));
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
                                        unique(transformToJooqFields(domainTypeDefinition, index.getFields())));
                    }
                }

                sqls.add(query.getSQL(ParamType.INLINED));
            }
        }

        return sqls;
    }

    protected List<org.jooq.Field> getJooqFields(Set<Field> fields) {
        List<org.jooq.Field> jooqFields = new ArrayList<>();

        for (Field field : fields) {
            String fieldName = LiteQL.StringUtils.camelNameToLowerDashConnectedLowercaseName(field.getName());

            if (field instanceof ReferenceField) {
                if (((ReferenceField) field).isCollection()) {
                    continue;
                }

                fieldName += "_" + IdField.ID_FIELD_NAME;
            }

            jooqFields.add(DSL.field(fieldName, getJooqDataType(field)));
        }

        return jooqFields;
    }

    private DataType getJooqDataType(Field field) {
        switch (field.getType()) {
            case Id:
                return JooqUtil.getStringDataType(false, IdField.ID_FIELD_LENGTH);
            case String:
                StringField stringField = (StringField) field;

                return JooqUtil.getStringDataType(stringField.isNullable(), stringField.getLength());
            case Long:
                LongField longField = (LongField) field;

                return JooqUtil.getLongDataType(longField.isNullable());
            case Integer:
                IntegerField integerField = (IntegerField) field;

                return JooqUtil.getIntegerDataType(integerField.isNullable());
            case Timestamp:
                TimestampField timestampField = (TimestampField) field;

                return JooqUtil.getTimestampDataType(timestampField.isNullable());
            case Boolean:
                return JooqUtil.getBooleanDataType();
            case Decimal:
                DecimalField decimalField = (DecimalField) field;

                return JooqUtil.getBigDecimalDataType(decimalField.isNullable());
            case Clob:
                ClobField clobField = (ClobField) field;
                return JooqUtil.getClobDataType(clobField.isNullable());
            case Blob:
                BlobField blobField = (BlobField) field;
                return JooqUtil.getBlobDataType(blobField.isNullable());
            case Reference:
                return JooqUtil.getStringDataType(((ReferenceField) field).isNullable(), 128);
            default:
                throw new IllegalArgumentException("Unsupported field type " + field.getClass().getSimpleName());
        }
    }

    private org.jooq.Field[] transformToJooqFields(
            DomainTypeDefinition domainTypeDefinition, Set<String> fieldNames) {
        Set<Field> fields = new LinkedHashSet<>();

        for (String fieldName : fieldNames) {
            boolean exist = false;

            for (Field field : domainTypeDefinition.getFields()) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    fields.add(field);

                    exist = true;
                }
            }

            if (!exist) {
                throw new IllegalStateException("Can not find Field [" + fieldName + "]");
            }
        }

        return getJooqFields(fields).toArray(new org.jooq.Field[fieldNames.size()]);
    }

}
