package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.enums.Database;
import org.cheeryworks.liteql.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.model.enums.IndexType;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.BlobField;
import org.cheeryworks.liteql.model.type.field.ClobField;
import org.cheeryworks.liteql.model.type.field.DecimalField;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.LongField;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.type.field.TimestampField;
import org.cheeryworks.liteql.model.type.index.AbstractIndex;
import org.cheeryworks.liteql.model.type.migration.operation.AbstractIndexMigrationOperation;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.sql.AbstractSqlParser;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.service.util.StringEncoder;
import org.cheeryworks.liteql.util.JOOQUtil;
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

import static org.cheeryworks.liteql.util.DatabaseUtil.INDEX_KEY_PREFIX;
import static org.cheeryworks.liteql.util.DatabaseUtil.PRIMARY_KEY_PREFIX;
import static org.cheeryworks.liteql.util.DatabaseUtil.UNIQUE_KEY_PREFIX;
import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.table;

public abstract class AbstractJooqSqlParser extends AbstractSqlParser {

    private Repository repository;

    private DSLContext dslContext;

    private Database database;

    public AbstractJooqSqlParser(Repository repository, DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(sqlCustomizer);

        this.repository = repository;
        this.dslContext = dslContext;
        this.database = JOOQUtil.getDatabase(dslContext.dialect());
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
        String tableName = getSqlCustomizer().getTableName(domainTypeName);

        DomainType domainType = getRepository().getDomainType(domainTypeName);

        List<String> sqls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(indexMigrationOperation.getIndexes())) {
            for (AbstractIndex index : indexMigrationOperation.getIndexes()) {
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
                                .on(table(tableName), transformToJooqFields(domainType, index.getFields()));
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
                                        unique(transformToJooqFields(domainType, index.getFields())));
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
            String fieldName = StringUtil.camelNameToLowerDashConnectedLowercaseName(field.getName());

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
                return JOOQDataType.getStringDataType(false, IdField.ID_FIELD_LENGTH);
            case String:
                StringField stringField = (StringField) field;

                return JOOQDataType.getStringDataType(stringField.isNullable(), stringField.getLength());
            case Long:
                LongField longField = (LongField) field;

                return JOOQDataType.getLongDataType(longField.isNullable());
            case Integer:
                IntegerField integerField = (IntegerField) field;

                return JOOQDataType.getIntegerDataType(integerField.isNullable());
            case Timestamp:
                TimestampField timestampField = (TimestampField) field;

                return JOOQDataType.getTimestampDataType(timestampField.isNullable());
            case Boolean:
                return JOOQDataType.getBooleanDataType();
            case Decimal:
                DecimalField decimalField = (DecimalField) field;

                return JOOQDataType.getBigDecimalDataType(decimalField.isNullable());
            case Clob:
                ClobField clobField = (ClobField) field;
                return JOOQDataType.getClobDataType(clobField.isNullable());
            case Blob:
                BlobField blobField = (BlobField) field;
                return JOOQDataType.getBlobDataType(blobField.isNullable());
            case Reference:
                return JOOQDataType.getStringDataType(((ReferenceField) field).isNullable(), 128);
            default:
                throw new IllegalArgumentException("Unsupported field type " + field.getClass().getSimpleName());
        }
    }

    private org.jooq.Field[] transformToJooqFields(DomainType domainType, Set<String> fieldNames) {
        Set<Field> fields = new LinkedHashSet<>();

        for (String fieldName : fieldNames) {
            boolean exist = false;

            for (Field field : domainType.getFields()) {
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
