package org.cheeryworks.liteql.service.query.jooq;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.query.QueryCondition;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.delete.DeleteQuery;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.query.read.PageReadQuery;
import org.cheeryworks.liteql.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.query.read.page.PageRequest;
import org.cheeryworks.liteql.query.read.page.Pageable;
import org.cheeryworks.liteql.query.read.sort.QuerySort;
import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.query.save.CreateQuery;
import org.cheeryworks.liteql.query.save.UpdateQuery;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.schema.index.Unique;
import org.cheeryworks.liteql.service.jooq.AbstractJooqParser;
import org.cheeryworks.liteql.service.query.sql.InlineSqlDeleteQuery;
import org.cheeryworks.liteql.service.query.sql.InlineSqlReadQuery;
import org.cheeryworks.liteql.service.query.sql.InlineSqlSaveQuery;
import org.cheeryworks.liteql.service.query.sql.SqlQueryParser;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.sql.SqlDeleteQuery;
import org.cheeryworks.liteql.sql.SqlReadQuery;
import org.cheeryworks.liteql.sql.SqlSaveQuery;
import org.cheeryworks.liteql.util.JooqUtil;
import org.cheeryworks.liteql.util.SqlQueryServiceUtil;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DeleteFinalStep;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class JooqQueryParser extends AbstractJooqParser implements SqlQueryParser {

    private static final String TABLE_ALIAS_PREFIX = "a";

    private static final RandomBasedGenerator UUID_GENERATOR = Generators.randomBasedGenerator();

    public JooqQueryParser(
            LiteQLProperties liteQLProperties, SchemaService schemaService,
            DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, schemaService, dslContext, sqlCustomizer);
    }

    @Override
    public SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery) {
        SqlReadQuery sqlReadQuery = new InlineSqlReadQuery();

        List<Condition> conditions = new LinkedList<>();

        if (readQuery.getConditions() != null) {
            conditions.add(
                    JooqUtil.getCondition(
                            readQuery.getDomainTypeName(), TABLE_ALIAS_PREFIX,
                            readQuery.getConditions(), getSqlCustomizer()));
        }

        Set<String> accessDecisionFields = getAccessDecisionFields(readQuery);

        if (!accessDecisionFields.isEmpty()) {
            conditions.add(
                    JooqUtil.getCondition(
                            readQuery.getDomainTypeName(), TABLE_ALIAS_PREFIX,
                            readQuery.getAccessDecisionConditions(), getSqlCustomizer()));
        }

        Condition condition = JooqUtil.getCondition(
                readQuery.getDomainTypeName(), TABLE_ALIAS_PREFIX,
                readQuery.getConditions(), getSqlCustomizer());

        List<org.jooq.Field<Object>> fields = getSelectFields(
                getSchemaService().getDomainTypeDefinition(readQuery.getDomainTypeName()),
                readQuery.getFields(), TABLE_ALIAS_PREFIX, sqlReadQuery);

        List<JooqJoinedTable> joinedTables = parseJoins(
                readQuery.getDomainTypeName(), TABLE_ALIAS_PREFIX, readQuery.getJoins(), sqlReadQuery);

        if (CollectionUtils.isNotEmpty(joinedTables)) {
            for (JooqJoinedTable joinedTable : joinedTables) {
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
                .from(table(getSqlCustomizer().getTableName(readQuery.getDomainTypeName())).as(TABLE_ALIAS_PREFIX));

        if (CollectionUtils.isNotEmpty(joinedTables)) {
            for (JooqJoinedTable joinedTable : joinedTables) {
                selectJoinStep
                        .leftOuterJoin(table(joinedTable.getTableName()).as(joinedTable.getTableAlias()))
                        .on(joinedTable.getJoinCondition());
            }
        }

        SelectConditionStep selectConditionStep = selectJoinStep.where(condition);

        sqlReadQuery.setTotalSql(getDslContext().select(count()).from(selectConditionStep).getSQL());
        sqlReadQuery.setTotalSqlParameters(selectConditionStep.getBindValues().toArray());

        if (CollectionUtils.isNotEmpty(readQuery.getSorts())) {
            List<QuerySort> querySorts = readQuery.getSorts();

            for (QuerySort querySort : querySorts) {
                selectConditionStep.orderBy(
                        field(getSqlCustomizer().getColumnName(readQuery.getDomainTypeName(), querySort.getField()))
                                .sort(SortOrder.valueOf(querySort.getDirection().name())));
            }
        }

        if (readQuery instanceof PageReadQuery) {
            Pageable pageable = transformPage(
                    ((PageReadQuery) readQuery).getPage(),
                    ((PageReadQuery) readQuery).getSize());

            sqlReadQuery.setSql(JooqUtil.getPageSql(getDatabase(), selectConditionStep, pageable));
        } else {
            sqlReadQuery.setSql(selectConditionStep.getSQL());
        }

        sqlReadQuery.setSqlParameters(selectConditionStep.getBindValues().toArray());

        return sqlReadQuery;
    }

    private Set<String> getAccessDecisionFields(AbstractTypedReadQuery readQuery) {
        Set<String> fields = new LinkedHashSet<>();

        if (readQuery.getAccessDecisionConditions() != null) {
            fields.addAll(getAccessDecisionFields(readQuery.getAccessDecisionConditions()));
        }

        return fields;
    }

    private Set<String> getAccessDecisionFields(QueryConditions accessDecisionConditions) {
        Set<String> fields = new LinkedHashSet<>();

        for (QueryCondition accessDecisionCondition : accessDecisionConditions) {
            fields.add(accessDecisionCondition.getField());

            accessDecisionCondition.setField(accessDecisionCondition.getField() + "ADF");

            if (accessDecisionCondition.getConditions() != null) {
                fields.addAll(getAccessDecisionFields(accessDecisionCondition.getConditions()));
            }
        }

        return fields;
    }

    private Pageable transformPage(int page, int size) {
        return new PageRequest(page == 0 ? 0 : page - 1, size);
    }

    private List<JooqJoinedTable> parseJoins(
            TypeName parentDomainTypeName, String parentTableAliasPrefix,
            List<JoinedReadQuery> joinedReadQueries, SqlReadQuery sqlReadQuery) {
        List<JooqJoinedTable> joinedTables = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(joinedReadQueries)) {
            for (JoinedReadQuery joinedReadQuery : joinedReadQueries) {
                JooqJoinedTable joinedTable = new JooqJoinedTable();
                joinedTable.setTableName(getSqlCustomizer().getTableName(joinedReadQuery.getDomainTypeName()));
                joinedTable.setTableAlias(parentTableAliasPrefix + joinedTables.size());
                joinedTable.setFields(
                        getSelectFields(
                                getSchemaService().getDomainTypeDefinition(joinedReadQuery.getDomainTypeName()),
                                joinedReadQuery.getFields(), joinedTable.getTableAlias(), sqlReadQuery));

                QueryConditions joinConditions = new QueryConditions();

                joinConditions.add(new QueryCondition(
                        IdField.ID_FIELD_NAME,
                        ConditionClause.EQUALS, ConditionType.Field,
                        joinedReadQuery.getDomainTypeName().getName()));

                joinedTable.setJoinCondition(
                        JooqUtil.getCondition(
                                parentDomainTypeName, parentTableAliasPrefix,
                                joinedReadQuery.getDomainTypeName(), joinedTable.getTableAlias(),
                                joinConditions, getSqlCustomizer()));

                joinedTable.setCondition(
                        JooqUtil.getCondition(
                                joinedReadQuery.getDomainTypeName(), joinedTable.getTableAlias(),
                                joinedReadQuery.getConditions(), getSqlCustomizer()));

                joinedTables.add(joinedTable);

                if (CollectionUtils.isNotEmpty(joinedReadQuery.getJoins())) {
                    joinedTables.addAll(
                            parseJoins(
                                    joinedReadQuery.getDomainTypeName(), joinedTable.getTableAlias(),
                                    joinedReadQuery.getJoins(), sqlReadQuery));
                }
            }
        }

        return joinedTables;
    }

    private List<org.jooq.Field<Object>> getSelectFields(
            DomainTypeDefinition domainTypeDefinition, FieldDefinitions fieldDefinitions,
            String tableAlias, SqlReadQuery sqlReadQuery) {
        List<org.jooq.Field<Object>> fields = new ArrayList<>();

        if (MapUtils.isEmpty(sqlReadQuery.getFields())) {
            sqlReadQuery.setFields(new HashMap<>());
        }

        if (TABLE_ALIAS_PREFIX.equals(tableAlias) && CollectionUtils.isEmpty(fieldDefinitions)) {
            for (Field field : domainTypeDefinition.getFields()) {
                if (field instanceof ReferenceField) {
                    if (((ReferenceField) field).isCollection()) {
                        continue;
                    }
                }

                String columnName = getSqlCustomizer().getColumnName(
                        domainTypeDefinition.getTypeName(), field.getName());

                fields.add(field(tableAlias + "." + columnName));

                sqlReadQuery.getFields().put(columnName.toLowerCase(), field.getName());
            }
        } else if (CollectionUtils.isNotEmpty(fieldDefinitions)) {
            for (FieldDefinition fieldDefinition : fieldDefinitions) {
                String columnName = getSqlCustomizer().getColumnName(
                        domainTypeDefinition.getTypeName(), fieldDefinition.getName());

                fields.add(field(tableAlias + "." + columnName).as(fieldDefinition.getAlias()));

                sqlReadQuery.getFields().put(fieldDefinition.getAlias().toLowerCase(), fieldDefinition.getAlias());
            }
        }

        return fields;
    }

    @Override
    public SqlSaveQuery getSqlSaveQuery(AbstractSaveQuery saveQuery, DomainTypeDefinition domainTypeDefinition) {
        Map<String, Object> data = saveQuery.getData();

        SqlSaveQuery sqlSaveQuery = new InlineSqlSaveQuery();

        Map<String, Class> fieldDefinitions = SqlQueryServiceUtil.getFieldDefinitions(domainTypeDefinition);

        if (saveQuery instanceof CreateQuery) {
            InsertSetStep insertSetStep = getDslContext()
                    .insertInto(table(getSqlCustomizer().getTableName(saveQuery.getDomainTypeName())));

            DataType dataType = JooqUtil.getDataType(fieldDefinitions.get(IdField.ID_FIELD_NAME));

            InsertSetMoreStep insertSetMoreStep = insertSetStep.set(
                    field("id", dataType),
                    UUID_GENERATOR.generate());

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                dataType = JooqUtil.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (domainTypeDefinition.isReferenceField(dataEntry.getKey())) {
                    String fieldName = getSqlCustomizer().getColumnName(
                            domainTypeDefinition.getTypeName(), dataEntry.getKey());

                    if (dataEntry.getValue() instanceof Map) {
                        insertSetMoreStep.set(
                                field(fieldName, dataType),
                                getReferenceIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainTypeDefinition));
                    } else {
                        insertSetMoreStep.set(field(fieldName, dataType), dataEntry.getValue());
                    }
                } else {
                    insertSetMoreStep.set(
                            field(getSqlCustomizer().getColumnName(
                                    domainTypeDefinition.getTypeName(), dataEntry.getKey()), dataType),
                            dataEntry.getValue());
                }
            }

            InsertFinalStep insertFinalStep = (InsertFinalStep) insertSetStep;

            sqlSaveQuery.setSql(insertFinalStep.getSQL());
            sqlSaveQuery.setSqlParameters(insertFinalStep.getBindValues().toArray());
        } else if (saveQuery instanceof UpdateQuery) {
            UpdateSetStep updateSetStep = getDslContext()
                    .update(table(getSqlCustomizer().getTableName(saveQuery.getDomainTypeName())));

            Unique uniqueKey = getUniqueKey(domainTypeDefinition, data);

            Condition condition = DSL.trueCondition();

            if (saveQuery.getAccessDecisionConditions() != null) {
                condition = condition.and(
                        JooqUtil.getCondition(
                                saveQuery.getDomainTypeName(), null,
                                saveQuery.getAccessDecisionConditions(), getSqlCustomizer()));
            }

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                DataType dataType = JooqUtil.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (uniqueKey.getFields().contains(dataEntry.getKey())) {
                    condition = condition.and(field(dataEntry.getKey()).eq(dataEntry.getValue()));

                    condition = condition.and(
                            field(getSqlCustomizer().getColumnName(
                                    domainTypeDefinition.getTypeName(),
                                    dataEntry.getKey()), dataType).eq(dataEntry.getValue()));
                } else {
                    if (domainTypeDefinition.isReferenceField(dataEntry.getKey())) {
                        String fieldName = getSqlCustomizer().getColumnName(
                                domainTypeDefinition.getTypeName(), dataEntry.getKey());

                        if (dataEntry.getValue() instanceof Map) {
                            updateSetStep.set(
                                    field(fieldName, dataType),
                                    getReferenceIdSelect(
                                            dataEntry.getKey(), dataEntry.getValue(), domainTypeDefinition));
                        } else {
                            updateSetStep.set(field(fieldName, dataType), dataEntry.getValue());
                        }
                    } else {
                        updateSetStep.set(
                                field(getSqlCustomizer().getColumnName(
                                        domainTypeDefinition.getTypeName(), dataEntry.getKey()), dataType),
                                dataEntry.getValue());
                    }
                }
            }

            UpdateFinalStep updateFinalStep = ((UpdateSetMoreStep) updateSetStep).where(condition);

            sqlSaveQuery.setSql(updateFinalStep.getSQL());
            sqlSaveQuery.setSqlParameters(updateFinalStep.getBindValues().toArray());
        } else {
            throw new IllegalArgumentException(
                    "Unsupported query domainTypeDefinition " + saveQuery.getClass().getSimpleName());
        }

        return sqlSaveQuery;
    }

    private Object getReferenceIdSelect(
            String fieldName, Object fieldValue, DomainTypeDefinition domainTypeDefinition) {
        Condition condition = DSL.trueCondition();

        for (Map.Entry<String, Object> fieldValueEntry : ((Map<String, Object>) fieldValue).entrySet()) {
            condition = condition.and(field(fieldValueEntry.getKey()).eq(fieldValueEntry.getValue()));
        }

        return getDslContext()
                .select(
                        field("id"))
                .from(table(getSqlCustomizer().getTableName(
                        new TypeName(domainTypeDefinition.getTypeName().getSchema(), fieldName))))
                .where(condition).asField();
    }

    private Unique getUniqueKey(DomainTypeDefinition domainTypeDefinition, Map<String, Object> data) {
        for (Unique uniqueKey : domainTypeDefinition.getUniques()) {
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
                "No unique key matched in data " + data
                        + " for domainTypeDefinition [" + domainTypeDefinition.getTypeName().getFullname() + "]");
    }

    @Override
    public SqlDeleteQuery getSqlDeleteQuery(DeleteQuery deleteQuery) {
        List<Condition> conditions = new ArrayList<>();

        conditions.add(
                JooqUtil.getCondition(
                        deleteQuery.getDomainTypeName(), null,
                        deleteQuery.getConditions(), getSqlCustomizer()));

        if (deleteQuery.getAccessDecisionConditions() != null) {
            conditions.add(
                    JooqUtil.getCondition(
                            deleteQuery.getDomainTypeName(), null,
                            deleteQuery.getAccessDecisionConditions(), getSqlCustomizer()));
        }

        DeleteFinalStep deleteFinalStep = getDslContext()
                .deleteFrom(table(getSqlCustomizer().getTableName(deleteQuery.getDomainTypeName())))
                .where(conditions);

        InlineSqlDeleteQuery sqlDeleteQuery = new InlineSqlDeleteQuery();
        sqlDeleteQuery.setSql(deleteFinalStep.getSQL());
        sqlDeleteQuery.setSqlParameters(deleteFinalStep.getBindValues().toArray());

        return sqlDeleteQuery;
    }

}
