package org.cheeryworks.liteql.sql.jooq;

import org.cheeryworks.liteql.model.type.DomainTypeField;
import org.cheeryworks.liteql.model.type.DomainTypeUniqueKey;
import org.cheeryworks.liteql.model.type.field.AssociationField;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.jooq.util.JOOQDataTypeUtil;
import org.cheeryworks.liteql.sql.jooq.util.JOOQDatabaseTypeUtil;
import org.cheeryworks.liteql.sql.util.StringEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.AlterTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.cheeryworks.liteql.sql.type.SqlSchemaParser.PRIMARY_KEY_PREFIX;
import static org.cheeryworks.liteql.sql.type.SqlSchemaParser.UNIQUE_KEY_PREFIX;
import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;

public abstract class AbstractJooqSqlParser {

    private Repository repository;

    private Database database;

    private DSLContext dslContext;

    public AbstractJooqSqlParser(Repository repository, Database database) {
        this.repository = repository;
        this.database = database;

        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderNameStyle(RenderNameStyle.LOWER);

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
                        primaryKey(DSL.field(DomainTypeField.ID_FIELD_NAME)));

        return alterTableFinalStep.getSQL();
    }

    protected List<String> parsingAddUniques(String tableName, List<DomainTypeUniqueKey> uniques) {
        List<String> sqls = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(uniques)) {
            for (DomainTypeUniqueKey uniqueKey : uniques) {
                String fieldsInString = Arrays.toString(
                        uniqueKey.getFields().toArray(new String[uniqueKey.getFields().size()]));
                String uniqueKeyName = UNIQUE_KEY_PREFIX
                        + StringEncoder.md5(tableName + "_" + fieldsInString).substring(0, 20);

                AlterTableFinalStep alterTableFinalStep = getDslContext()
                        .alterTable(tableName)
                        .add(constraint(uniqueKeyName).
                                unique(uniqueKey.getFields().toArray(
                                        new String[uniqueKey.getFields().size()])));

                sqls.add(alterTableFinalStep.getSQL());
            }
        }

        return sqls;
    }

    protected List<String> parsingDropUniques(String tableName, List<DomainTypeUniqueKey> uniques) {
        List<String> sqls = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(uniques)) {
            for (DomainTypeUniqueKey uniqueKey : uniques) {
                String fieldsInString = Arrays.toString(
                        uniqueKey.getFields().toArray(new String[uniqueKey.getFields().size()]));
                String uniqueKeyName = UNIQUE_KEY_PREFIX
                        + StringEncoder.md5(tableName + "_" + fieldsInString).substring(0, 20);

                AlterTableFinalStep alterTableFinalStep = getDslContext()
                        .alterTable(tableName)
                        .dropConstraint(uniqueKeyName);

                sqls.add(alterTableFinalStep.getSQL());
            }
        }

        return sqls;
    }

    protected List<org.jooq.Field> getJooqFields(List<DomainTypeField> fields, Database database) {
        List<org.jooq.Field> jooqFields = new ArrayList<Field>();

        for (DomainTypeField field : fields) {
            String fieldName = field.getName();

            if (field instanceof AssociationField) {
                fieldName += "_" + DomainTypeField.ID_FIELD_NAME;
            }

            jooqFields.add(DSL.field(fieldName, getJooqDataType(field, database)));
        }

        return jooqFields;
    }

    private DataType getJooqDataType(DomainTypeField field, Database database) {
        if (field instanceof IdField) {
            return JOOQDataTypeUtil.getInstance(database).getStringDataType().length(128).nullable(false);
        } else if (field instanceof AssociationField) {
            return JOOQDataTypeUtil.getInstance(database).getStringDataType().length(128);
        } else if (field instanceof StringField) {
            StringField stringField = (StringField) field;
            DataType dataType = JOOQDataTypeUtil
                    .getInstance(database)
                    .getStringDataType()
                    .length(stringField.getLength());

            if (stringField.getNullable() != null && BooleanUtils.isNotTrue(stringField.getNullable())) {
                dataType = dataType.nullable(false);
            }

            return dataType;
        } else if (field instanceof IntegerField) {
            IntegerField integerField = (IntegerField) field;
            DataType dataType = JOOQDataTypeUtil
                    .getInstance(database)
                    .getIntegerDataType();

            if (integerField.getNullable() != null && BooleanUtils.isNotTrue(integerField.getNullable())) {
                dataType = dataType.nullable(false);
            }

            return dataType;
        }

        throw new IllegalArgumentException("Unsupported field type " + field.getClass().getSimpleName());
    }

    public static String getTableName(String schemaName, String domainTypeName) {
        return StringUtils.lowerCase(schemaName + "_" + domainTypeName);
    }

}