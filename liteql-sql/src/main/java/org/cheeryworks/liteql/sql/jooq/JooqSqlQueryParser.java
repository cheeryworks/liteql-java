package org.cheeryworks.liteql.sql.jooq;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import org.cheeryworks.liteql.model.enums.StandardConditionClause;
import org.cheeryworks.liteql.model.query.ReadQuery;
import org.cheeryworks.liteql.model.query.CreateQuery;
import org.cheeryworks.liteql.model.query.DeleteQuery;
import org.cheeryworks.liteql.model.query.PageReadQuery;
import org.cheeryworks.liteql.model.query.SaveQuery;
import org.cheeryworks.liteql.model.query.UpdateQuery;
import org.cheeryworks.liteql.model.query.condition.QueryCondition;
import org.cheeryworks.liteql.model.query.condition.type.FieldConditionType;
import org.cheeryworks.liteql.model.query.field.QueryFieldDefinition;
import org.cheeryworks.liteql.model.query.field.QueryFieldDefinitions;
import org.cheeryworks.liteql.model.query.join.JoinedQuery;
import org.cheeryworks.liteql.model.query.page.PageRequest;
import org.cheeryworks.liteql.model.query.page.Pageable;
import org.cheeryworks.liteql.model.query.sort.QuerySort;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.DomainTypeField;
import org.cheeryworks.liteql.model.type.DomainTypeUniqueKey;
import org.cheeryworks.liteql.model.type.field.AssociationField;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.sql.jooq.util.JOOQDataTypeUtil;
import org.cheeryworks.liteql.sql.jooq.util.JOOQPageUtil;
import org.cheeryworks.liteql.sql.query.InlineSqlDeleteQuery;
import org.cheeryworks.liteql.sql.query.InlineSqlReadQuery;
import org.cheeryworks.liteql.sql.query.InlineSqlSaveQuery;
import org.cheeryworks.liteql.sql.query.SqlDeleteQuery;
import org.cheeryworks.liteql.sql.query.SqlQueryParser;
import org.cheeryworks.liteql.sql.query.SqlReadQuery;
import org.cheeryworks.liteql.sql.query.SqlSaveQuery;
import org.cheeryworks.liteql.sql.query.join.JoinedTable;
import org.cheeryworks.liteql.sql.util.SqlQueryServiceUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.DeleteFinalStep;
import org.jooq.Field;
import org.jooq.InsertFinalStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SortOrder;
import org.jooq.UpdateFinalStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.UpdateSetStep;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class JooqSqlQueryParser extends AbstractJooqSqlParser implements SqlQueryParser {

    private static final int DEFAULT_PAGE = 1;

    private static final int DEFAULT_PAGE_SIZE = 20;

    private static final String TABLE_ALIAS_PREFIX = "a";

    private static final RandomBasedGenerator UUID_GENERATOR = Generators.randomBasedGenerator();

    private static Logger logger = LoggerFactory.getLogger(JooqSqlQueryParser.class);

    public JooqSqlQueryParser(Repository repository, Database database) {
        super(repository, database);
    }

    @Override
    public SqlReadQuery getSqlReadQuery(ReadQuery readQuery) {
        SqlReadQuery sqlReadQuery = new InlineSqlReadQuery();

        Condition condition = getCondition(readQuery.getConditions(), null, TABLE_ALIAS_PREFIX);

        List<Field<Object>> fields = getSelectFields(
                readQuery.getDomainType(), readQuery.getFields(), TABLE_ALIAS_PREFIX, sqlReadQuery);

        List<JoinedTable> joinedTables = parseJoins(readQuery.getJoins(), TABLE_ALIAS_PREFIX, sqlReadQuery);

        if (CollectionUtils.isNotEmpty(joinedTables)) {
            for (JoinedTable joinedTable : joinedTables) {
                fields.addAll(joinedTable.getFields());

                if (joinedTable.getCondition() != null) {
                    if (condition != null) {
                        condition = condition.and(joinedTable.getCondition());
                    } else {
                        condition = joinedTable.getCondition();
                    }
                }
            }
        }

        SelectJoinStep selectJoinStep = getDslContext()
                .select(fields)
                .from(table(getTableName(readQuery.getDomainType())).as(TABLE_ALIAS_PREFIX));

        if (CollectionUtils.isNotEmpty(joinedTables)) {
            for (JoinedTable joinedTable : joinedTables) {
                selectJoinStep
                        .leftOuterJoin(table(joinedTable.getTableName()).as(joinedTable.getTableAlias()))
                        .on(joinedTable.getJoinCondition());
            }
        }

        SelectConditionStep selectConditionStep = selectJoinStep.where(condition);

        sqlReadQuery.setTotalSql(getDslContext().select(count()).from(selectConditionStep).getSQL());
        sqlReadQuery.setTotalSqlParameters(selectConditionStep.getBindValues());

        if (CollectionUtils.isNotEmpty(readQuery.getSorts())) {
            for (QuerySort querySort : readQuery.getSorts()) {
                selectConditionStep.orderBy(
                        field(SqlQueryServiceUtil.getColumnNameByFieldName(querySort.getField()))
                                .sort(SortOrder.valueOf(querySort.getDirection().name())));
            }
        }

        if (readQuery instanceof PageReadQuery) {
            Pageable pageable = transformPage(
                    ((PageReadQuery) readQuery).getPage(),
                    ((PageReadQuery) readQuery).getSize());

            sqlReadQuery.setSql(JOOQPageUtil.getPageSql(getDatabase(), selectConditionStep, pageable));
        } else {
            sqlReadQuery.setSql(selectConditionStep.getSQL());
        }

        sqlReadQuery.setSqlParameters(selectConditionStep.getBindValues());

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            logger.info("SqlReadQuery:\n" + sqlReadQuery.toString());
        }

        return sqlReadQuery;
    }

    private Pageable transformPage(int page, int size) {
        return new PageRequest(page == 0 ? 0 : page - 1, size);
    }

    private List<JoinedTable> parseJoins(
            List<JoinedQuery> joinedQueries, String joinedTableAliasPrefix, SqlReadQuery sqlReadQuery) {
        List<JoinedTable> joinedTables = new ArrayList<JoinedTable>();

        if (CollectionUtils.isNotEmpty(joinedQueries)) {
            for (JoinedQuery joinedQuery : joinedQueries) {
                JoinedTable joinedTable = new JoinedTable();
                joinedTable.setTableName(getTableName(joinedQuery.getDomainType()));
                joinedTable.setTableAlias(joinedTableAliasPrefix + joinedTables.size());
                joinedTable.setFields(
                        getSelectFields(
                                joinedQuery.getDomainType(), joinedQuery.getFields(),
                                joinedTable.getTableAlias(), sqlReadQuery));

                List<QueryCondition> joinConditions = new LinkedList<QueryCondition>();
                joinConditions.add(new QueryCondition(
                        DomainTypeField.ID_FIELD_NAME,
                        StandardConditionClause.EQUALS, new FieldConditionType(),
                        getDomainType(joinedQuery.getDomainType()).getName()
                                + StringUtils.capitalize(DomainTypeField.ID_FIELD_NAME)));
                joinedTable.setJoinCondition(
                        getCondition(
                                joinConditions,
                                joinedTableAliasPrefix, joinedTable.getTableAlias()));

                joinedTable.setCondition(
                        getCondition(joinedQuery.getConditions(), null, joinedTable.getTableAlias()));

                joinedTables.add(joinedTable);

                if (CollectionUtils.isNotEmpty(joinedQuery.getJoins())) {
                    joinedTables.addAll(parseJoins(joinedQuery.getJoins(), joinedTable.getTableAlias(), sqlReadQuery));
                }
            }
        }

        return joinedTables;
    }

    private List<Field<Object>> getSelectFields(
            String domainTypeWithSchemaName, QueryFieldDefinitions fieldDefinitions,
            String tableAlias, SqlReadQuery sqlReadQuery) {
        List<Field<Object>> fields = new ArrayList<Field<Object>>();

        if (MapUtils.isEmpty(sqlReadQuery.getFields())) {
            sqlReadQuery.setFields(new HashMap<String, DomainTypeField>());
        }

        DomainType domainType = getDomainType(domainTypeWithSchemaName);

        if (TABLE_ALIAS_PREFIX.equals(tableAlias) && fieldDefinitions == null) {
            for (DomainTypeField field : domainType.getFields()) {
                if (field instanceof AssociationField || field instanceof IdField) {
                    continue;
                } else {
                    fields.add(
                            field(tableAlias + "." + SqlQueryServiceUtil.getColumnNameByFieldName(field.getName())));
                    sqlReadQuery.getFields().put(
                            SqlQueryServiceUtil.getColumnNameByFieldName(field.getName()), field);
                }
            }
        } else if (fieldDefinitions != null) {
            for (QueryFieldDefinition fieldDefinition : fieldDefinitions) {
                fields.add(
                        field(tableAlias + "."
                                + SqlQueryServiceUtil.getColumnNameByFieldName(fieldDefinition.getName()))
                                .as(SqlQueryServiceUtil.getColumnNameByFieldName(fieldDefinition.getAlias())));

                for (DomainTypeField field : domainType.getFields()) {
                    if (field.getName().equals(fieldDefinition.getName())) {
                        sqlReadQuery.getFields().put(
                                SqlQueryServiceUtil.getColumnNameByFieldName(fieldDefinition.getAlias()), field);
                    }
                }
            }
        }

        return fields;
    }

    private String getTableName(String domainType) {
        String schemaName = domainType.substring(0, domainType.indexOf("."));
        String domainTypeName = domainType.substring(domainType.indexOf(".") + 1);

        return getTableName(schemaName, domainTypeName);
    }

    private DomainType getDomainType(String domainTypeWithSchemaName) {
        String schemaName = domainTypeWithSchemaName.substring(0, domainTypeWithSchemaName.indexOf("."));
        String domainTypeName = domainTypeWithSchemaName.substring(domainTypeWithSchemaName.indexOf(".") + 1);

        return getRepository().getDomainType(schemaName, domainTypeName);
    }

    private Condition getCondition(List<QueryCondition> conditions, String parentTableAlias, String tableAlias) {
        if (CollectionUtils.isNotEmpty(conditions)) {
            return SqlQueryServiceUtil.getConditions(
                    conditions, JOOQDataTypeUtil.getInstance(getDatabase()), parentTableAlias, tableAlias);
        }

        return null;
    }

    @Override
    public SqlSaveQuery getSqlSaveQuery(SaveQuery saveQuery, DomainType domainType) {
        Map<String, Object> data = saveQuery.getData();

        SqlSaveQuery sqlSaveQuery = new InlineSqlSaveQuery();

        JOOQDataType jooqDataType = JOOQDataTypeUtil.getInstance(getDatabase());

        Map<String, Class> fieldDefinitions = SqlQueryServiceUtil.getFieldDefinitions(domainType);

        if (saveQuery instanceof CreateQuery) {
            InsertSetStep insertSetStep = getDslContext()
                    .insertInto(table(getTableName(saveQuery.getDomainType())));

            DataType<String> dataType = jooqDataType.getDataType(
                    fieldDefinitions.get(DomainTypeField.ID_FIELD_NAME));

            InsertSetMoreStep insertSetMoreStep = insertSetStep.set(
                    field("id", dataType),
                    UUID_GENERATOR.generate());

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                dataType = jooqDataType.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (domainType.isAssociation(dataEntry.getKey())) {
                    String fieldName = SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()
                            + StringUtils.capitalize(DomainTypeField.ID_FIELD_NAME));

                    if (dataEntry.getValue() instanceof Map) {
                        insertSetMoreStep.set(
                                field(fieldName, dataType),
                                getAssociationIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainType));
                    } else {
                        insertSetMoreStep.set(field(fieldName, dataType), dataEntry.getValue());
                    }
                } else {
                    insertSetMoreStep.set(
                            field(SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()), dataType),
                            dataEntry.getValue());
                }
            }

            InsertFinalStep insertFinalStep = (InsertFinalStep) insertSetStep;

            sqlSaveQuery.setSql(insertFinalStep.getSQL());
            sqlSaveQuery.setSqlParameters(insertFinalStep.getBindValues());
        } else if (saveQuery instanceof UpdateQuery) {
            UpdateSetStep updateSetStep = getDslContext()
                    .update(table(getTableName(saveQuery.getDomainType())));

            DomainTypeUniqueKey uniqueKey = getUniqueKey(domainType, data);

            Condition condition = DSL.trueCondition();

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                DataType dataType = jooqDataType.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (uniqueKey.getFields().contains(dataEntry.getKey())) {
                    condition = condition.and(field(dataEntry.getKey()).eq(dataEntry.getValue()));

                    condition = condition.and(
                            field(SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()), dataType)
                                    .eq(dataEntry.getValue()));
                } else {
                    if (domainType.isAssociation(dataEntry.getKey())) {
                        String fieldName = SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()
                                + StringUtils.capitalize(DomainTypeField.ID_FIELD_NAME));

                        if (dataEntry.getValue() instanceof Map) {
                            updateSetStep.set(
                                    field(fieldName, dataType),
                                    getAssociationIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainType));
                        } else {
                            updateSetStep.set(field(fieldName, dataType), dataEntry.getValue());
                        }
                    } else {
                        updateSetStep.set(
                                field(SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()), dataType),
                                dataEntry.getValue());
                    }
                }
            }

            UpdateFinalStep updateFinalStep = ((UpdateSetMoreStep) updateSetStep).where(condition);

            sqlSaveQuery.setSql(updateFinalStep.getSQL());
            sqlSaveQuery.setSqlParameters(updateFinalStep.getBindValues());
        } else {
            throw new IllegalArgumentException("Unsupported query domainType " + saveQuery.getClass().getSimpleName());
        }

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            logger.info(sqlSaveQuery.toString());
        }

        return sqlSaveQuery;
    }

    private Object getAssociationIdSelect(String fieldName, Object fieldValue, DomainType domainType) {
        Condition condition = DSL.trueCondition();

        for (Map.Entry<String, Object> fieldValueEntry : ((Map<String, Object>) fieldValue).entrySet()) {
            condition = condition.and(field(fieldValueEntry.getKey()).eq(fieldValueEntry.getValue()));
        }

        return getDslContext()
                .select(
                        field("id"))
                .from(table(getTableName(domainType.getSchema() + "." + fieldName)))
                .where(condition).asField();
    }

    private DomainTypeUniqueKey getUniqueKey(DomainType domainType, Map<String, Object> data) {
        for (DomainTypeUniqueKey uniqueKey : domainType.getUniques()) {
            boolean matched = true;

            for (String field : uniqueKey.getFields()) {
                if (!data.containsKey(field)) {
                    matched = false;
                }
            }

            if (matched) {
                return uniqueKey;
            }
        }

        throw new IllegalArgumentException(
                "No unique key matched in data " + data + " for domainType " + domainType.getName());
    }

    @Override
    public SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery) {
        DeleteFinalStep deleteFinalStep = getDslContext()
                .deleteFrom(table(getTableName(deleteQuery.getDomainType())))
                .where(getCondition(deleteQuery.getConditions(), null, null));

        InlineSqlDeleteQuery sqlDeleteQuery = new InlineSqlDeleteQuery();
        sqlDeleteQuery.setSql(deleteFinalStep.getSQL());
        sqlDeleteQuery.setSqlParameters(deleteFinalStep.getBindValues());

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            logger.info("SqlDeleteQuery:\n" + sqlDeleteQuery.toString());
        }

        return sqlDeleteQuery;
    }

}