package org.cheeryworks.liteql.jooq.service.query;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.cheeryworks.liteql.jooq.service.AbstractJooqParser;
import org.cheeryworks.liteql.jooq.util.JooqUtil;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.QueryCondition;
import org.cheeryworks.liteql.skeleton.query.QueryConditions;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionClause;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;
import org.cheeryworks.liteql.skeleton.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.skeleton.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.page.PageRequest;
import org.cheeryworks.liteql.skeleton.query.read.page.Pageable;
import org.cheeryworks.liteql.skeleton.query.read.sort.QuerySort;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;
import org.cheeryworks.liteql.skeleton.schema.field.ReferenceField;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;
import org.cheeryworks.liteql.skeleton.service.query.sql.InlineSqlDeleteQuery;
import org.cheeryworks.liteql.skeleton.service.query.sql.InlineSqlReadQuery;
import org.cheeryworks.liteql.skeleton.service.query.sql.InlineSqlSaveQuery;
import org.cheeryworks.liteql.skeleton.service.query.sql.SqlQueryParser;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.skeleton.sql.SqlDeleteQuery;
import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;
import org.cheeryworks.liteql.skeleton.sql.SqlSaveQuery;
import org.cheeryworks.liteql.skeleton.util.SqlQueryServiceUtil;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DeleteFinalStep;
import org.jooq.InsertFinalStep;
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

    private static final String ACCESS_DECISION_FIELD_ALIAS_SUFFIX = "ADF";

    private static final RandomBasedGenerator UUID_GENERATOR = Generators.randomBasedGenerator();

    public JooqQueryParser(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService, SqlCustomizer sqlCustomizer,
            DSLContext dslContext) {
        super(liteQLProperties, schemaService, sqlCustomizer, dslContext);
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

        List<org.jooq.Field<Object>> fields = getSelectFields(
                getSchemaService().getDomainTypeDefinition(readQuery.getDomainTypeName()),
                readQuery.getFields(), TABLE_ALIAS_PREFIX, sqlReadQuery);

        List<JooqJoinedTable> joinedTables = parseJoins(
                readQuery.getDomainTypeName(), TABLE_ALIAS_PREFIX, readQuery.getJoins(), sqlReadQuery);

        if (CollectionUtils.isNotEmpty(joinedTables)) {
            for (JooqJoinedTable joinedTable : joinedTables) {
                fields.addAll(joinedTable.getFields());

                if (joinedTable.getCondition() != null) {
                    conditions.add(joinedTable.getCondition());
                }
            }
        }

        if (!accessDecisionFields.isEmpty()) {
            for (String accessDecisionField : accessDecisionFields) {
                fields.add(
                        field(
                                TABLE_ALIAS_PREFIX + "." + getSqlCustomizer().getColumnName(
                                        readQuery.getDomainTypeName(), accessDecisionField))
                                .as(accessDecisionField + ACCESS_DECISION_FIELD_ALIAS_SUFFIX));
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

        SelectConditionStep selectConditionStep = selectJoinStep.where(conditions);

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

            sqlReadQuery.setSql(JooqUtil.getPageSql(selectConditionStep, pageable));
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

            accessDecisionCondition.setField(accessDecisionCondition.getField());

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

                if (CollectionUtils.isEmpty(joinedReadQuery.getJoinConditions())) {
                    joinConditions.add(new QueryCondition(
                            IdField.ID_FIELD_NAME,
                            ConditionClause.EQUALS, ConditionType.Field,
                            joinedReadQuery.getDomainTypeName().getName()));
                } else {
                    joinConditions.addAll(joinedReadQuery.getJoinConditions());
                }

                joinedTable.setJoinCondition(
                        JooqUtil.getCondition(
                                parentDomainTypeName, parentTableAliasPrefix,
                                joinedReadQuery.getDomainTypeName(), joinedTable.getTableAlias(),
                                joinConditions, getSqlCustomizer()));

                joinedTable.setCondition(
                        JooqUtil.getCondition(
                                parentDomainTypeName, parentTableAliasPrefix,
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

            DataType dataType = getDataType(
                    domainTypeDefinition.getTypeName(), fieldDefinitions, IdField.ID_FIELD_NAME);

            if (!data.containsKey(IdField.ID_FIELD_NAME)) {
                insertSetStep.set(
                        field(IdField.ID_FIELD_NAME, dataType), UUID_GENERATOR.generate().toString());
            } else {
                insertSetStep.set(
                        field(IdField.ID_FIELD_NAME, dataType), data.get(IdField.ID_FIELD_NAME));
            }

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                if (dataEntry.getKey().equals(IdField.ID_FIELD_NAME)) {
                    continue;
                }

                dataType = getDataType(domainTypeDefinition.getTypeName(), fieldDefinitions, dataEntry.getKey());

                String fieldName = getSqlCustomizer().getColumnName(
                        domainTypeDefinition.getTypeName(), dataEntry.getKey());

                if (domainTypeDefinition.isReferenceField(dataEntry.getKey())
                        && dataEntry.getValue() != null && dataEntry.getValue() instanceof Map) {
                    insertSetStep.set(
                            field(fieldName, dataType),
                            getReferenceIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainTypeDefinition));
                } else {
                    insertSetStep.set(field(fieldName, dataType), dataEntry.getValue());
                }
            }

            InsertFinalStep insertFinalStep = (InsertFinalStep) insertSetStep;

            sqlSaveQuery.setSql(insertFinalStep.getSQL());
            sqlSaveQuery.setSqlParameters(insertFinalStep.getBindValues().toArray());
        } else if (saveQuery instanceof UpdateQuery) {
            UpdateSetStep updateSetStep = getDslContext()
                    .update(table(getSqlCustomizer().getTableName(saveQuery.getDomainTypeName())));

            UniqueDefinition uniqueDefinition = null;

            if (!data.containsKey(IdField.ID_FIELD_NAME)) {
                uniqueDefinition = getUniqueDefinition(domainTypeDefinition, data);
            }

            Condition condition = DSL.trueCondition();

            if (saveQuery.getAccessDecisionConditions() != null) {
                condition = condition.and(
                        JooqUtil.getCondition(
                                saveQuery.getDomainTypeName(), null,
                                saveQuery.getAccessDecisionConditions(), getSqlCustomizer()));
            }

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                DataType dataType = getDataType(
                        domainTypeDefinition.getTypeName(), fieldDefinitions, dataEntry.getKey());

                String columnName = getSqlCustomizer().getColumnName(
                        domainTypeDefinition.getTypeName(), dataEntry.getKey());

                if (dataEntry.getKey().equals(IdField.ID_FIELD_NAME)
                        || (uniqueDefinition != null && uniqueDefinition.getFields().contains(dataEntry.getKey()))) {
                    condition = condition.and(field(columnName, dataType).eq(dataEntry.getValue()));
                } else {
                    if (domainTypeDefinition.isReferenceField(dataEntry.getKey())
                            && dataEntry.getValue() instanceof Map) {
                        updateSetStep.set(
                                field(columnName, dataType),
                                getReferenceIdSelect(
                                        dataEntry.getKey(), dataEntry.getValue(), domainTypeDefinition));
                    } else {
                        updateSetStep.set(field(columnName, dataType), dataEntry.getValue());
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

    private DataType getDataType(TypeName typeName, Map<String, Class> fieldDefinitions, String fieldName) {
        Class fieldType = fieldDefinitions.get(fieldName);

        if (fieldType == null) {
            throw new IllegalArgumentException(
                    "Field " + fieldName + " not defined in domain type " + typeName.getFullname());
        }

        return JooqUtil.getDataType(fieldType);
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

    private UniqueDefinition getUniqueDefinition(DomainTypeDefinition domainTypeDefinition, Map<String, Object> data) {
        for (UniqueDefinition uniqueDefinition : domainTypeDefinition.getUniques()) {
            boolean matched = true;

            for (String field : uniqueDefinition.getFields()) {
                if (!data.containsKey(field)) {
                    matched = false;
                }
            }

            if (matched) {
                return uniqueDefinition;
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

    private static class JooqJoinedTable {

        private String tableName;

        private String tableAlias;

        private List<org.jooq.Field<Object>> fields;

        private Condition joinCondition;

        private Condition condition;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getTableAlias() {
            return tableAlias;
        }

        public void setTableAlias(String tableAlias) {
            this.tableAlias = tableAlias;
        }

        public List<org.jooq.Field<Object>> getFields() {
            return fields;
        }

        public void setFields(List<org.jooq.Field<Object>> fields) {
            this.fields = fields;
        }

        public Condition getJoinCondition() {
            return joinCondition;
        }

        public void setJoinCondition(Condition joinCondition) {
            this.joinCondition = joinCondition;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

    }

}
