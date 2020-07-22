package org.cheeryworks.liteql.service.query.jooq;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.model.query.read.join.JoinedReadQuery;
import org.cheeryworks.liteql.model.query.read.page.PageRequest;
import org.cheeryworks.liteql.model.query.read.page.Pageable;
import org.cheeryworks.liteql.model.query.read.sort.QuerySort;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Unique;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.jooq.AbstractJooqSqlParser;
import org.cheeryworks.liteql.service.query.sql.InlineSqlDeleteQuery;
import org.cheeryworks.liteql.service.query.sql.InlineSqlReadQuery;
import org.cheeryworks.liteql.service.query.sql.InlineSqlSaveQuery;
import org.cheeryworks.liteql.service.query.sql.SqlQueryParser;
import org.cheeryworks.liteql.service.repository.Repository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class JooqSqlQueryParser extends AbstractJooqSqlParser implements SqlQueryParser {

    private static final String TABLE_ALIAS_PREFIX = "a";

    private static final RandomBasedGenerator UUID_GENERATOR = Generators.randomBasedGenerator();

    private static Logger logger = LoggerFactory.getLogger(JooqSqlQueryParser.class);

    public JooqSqlQueryParser(Repository repository, DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(repository, dslContext, sqlCustomizer);
    }

    @Override
    public SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery) {
        SqlReadQuery sqlReadQuery = new InlineSqlReadQuery();

        List<Condition> conditions = new LinkedList<>();

        Set<String> accessDecisionFields = new LinkedHashSet<>();

        if (readQuery.getConditions() != null) {
            conditions.add(
                    JooqUtil.getCondition(
                            readQuery.getDomainTypeName(), readQuery.getConditions(),
                            null, TABLE_ALIAS_PREFIX, getSqlCustomizer()));
        }

        accessDecisionFields = getAccessDecisionFields(readQuery);

        if (!accessDecisionFields.isEmpty()) {
            conditions.add(
                    JooqUtil.getCondition(
                            readQuery.getDomainTypeName(), readQuery.getAccessDecisionConditions(),
                            null, TABLE_ALIAS_PREFIX, getSqlCustomizer()));
        }

        Condition condition = JooqUtil.getCondition(
                readQuery.getDomainTypeName(), readQuery.getConditions(),
                null, TABLE_ALIAS_PREFIX, getSqlCustomizer());

        List<org.jooq.Field<Object>> fields = getSelectFields(
                getRepository().getDomainType(readQuery.getDomainTypeName()),
                readQuery.getFields(), TABLE_ALIAS_PREFIX, sqlReadQuery);

        List<JooqJoinedTable> joinedTables = parseJoins(readQuery.getJoins(), TABLE_ALIAS_PREFIX, sqlReadQuery);

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
        sqlReadQuery.setTotalSqlParameters(selectConditionStep.getBindValues());

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

        sqlReadQuery.setSqlParameters(selectConditionStep.getBindValues());

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            logger.info("SqlReadQuery:\n" + sqlReadQuery.toString());
        }

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
            List<JoinedReadQuery> joinedReadQueries, String joinedTableAliasPrefix, SqlReadQuery sqlReadQuery) {
        List<JooqJoinedTable> joinedTables = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(joinedReadQueries)) {
            for (JoinedReadQuery joinedReadQuery : joinedReadQueries) {
                JooqJoinedTable joinedTable = new JooqJoinedTable();
                joinedTable.setTableName(getSqlCustomizer().getTableName(joinedReadQuery.getDomainTypeName()));
                joinedTable.setTableAlias(joinedTableAliasPrefix + joinedTables.size());
                joinedTable.setFields(
                        getSelectFields(
                                getRepository().getDomainType(joinedReadQuery.getDomainTypeName()),
                                joinedReadQuery.getFields(), joinedTable.getTableAlias(), sqlReadQuery));

                QueryConditions joinConditions = new QueryConditions();

                joinConditions.add(new QueryCondition(
                        IdField.ID_FIELD_NAME,
                        ConditionClause.EQUALS, ConditionType.Field,
                        joinedReadQuery.getDomainTypeName().getName()
                                + StringUtils.capitalize(IdField.ID_FIELD_NAME)));

                joinedTable.setJoinCondition(
                        JooqUtil.getCondition(
                                joinedReadQuery.getDomainTypeName(),
                                joinConditions,
                                joinedTableAliasPrefix, joinedTable.getTableAlias(), getSqlCustomizer()));

                joinedTable.setCondition(
                        JooqUtil.getCondition(
                                joinedReadQuery.getDomainTypeName(),
                                joinedReadQuery.getConditions(),
                                null, joinedTable.getTableAlias(), getSqlCustomizer()));

                joinedTables.add(joinedTable);

                if (CollectionUtils.isNotEmpty(joinedReadQuery.getJoins())) {
                    joinedTables.addAll(
                            parseJoins(joinedReadQuery.getJoins(), joinedTable.getTableAlias(), sqlReadQuery));
                }
            }
        }

        return joinedTables;
    }

    private List<org.jooq.Field<Object>> getSelectFields(
            DomainType domainType, FieldDefinitions fieldDefinitions,
            String tableAlias, SqlReadQuery sqlReadQuery) {
        List<org.jooq.Field<Object>> fields = new ArrayList<>();

        if (MapUtils.isEmpty(sqlReadQuery.getFields())) {
            sqlReadQuery.setFields(new HashMap<>());
        }

        if (TABLE_ALIAS_PREFIX.equals(tableAlias) && fieldDefinitions == null) {
            for (Field field : domainType.getFields()) {
                if (field instanceof ReferenceField || field instanceof IdField) {
                    continue;
                } else {
                    fields.add(
                            field(tableAlias + "." + getSqlCustomizer().getColumnName(domainType, field.getName())));
                    sqlReadQuery.getFields().put(
                            getSqlCustomizer().getColumnName(domainType, field.getName()), field);
                }
            }
        } else if (fieldDefinitions != null) {
            for (FieldDefinition fieldDefinition : fieldDefinitions) {
                fields.add(
                        field(
                                tableAlias + "." + getSqlCustomizer().getColumnName(
                                        domainType, fieldDefinition.getName())
                        ).as(getSqlCustomizer().getColumnName(domainType, fieldDefinition.getAlias())));

                for (Field field : domainType.getFields()) {
                    if (field.getName().equals(fieldDefinition.getName())) {
                        sqlReadQuery.getFields().put(
                                getSqlCustomizer().getColumnName(domainType, fieldDefinition.getAlias()), field);
                    }
                }
            }
        }

        return fields;
    }

    @Override
    public SqlSaveQuery getSqlSaveQuery(AbstractSaveQuery saveQuery, DomainType domainType) {
        Map<String, Object> data = saveQuery.getData();

        SqlSaveQuery sqlSaveQuery = new InlineSqlSaveQuery();

        Map<String, Class> fieldDefinitions = SqlQueryServiceUtil.getFieldDefinitions(domainType);

        if (saveQuery instanceof CreateQuery) {
            InsertSetStep insertSetStep = getDslContext()
                    .insertInto(table(getSqlCustomizer().getTableName(saveQuery.getDomainTypeName())));

            DataType dataType = JooqUtil.getDataType(fieldDefinitions.get(IdField.ID_FIELD_NAME));

            InsertSetMoreStep insertSetMoreStep = insertSetStep.set(
                    field("id", dataType),
                    UUID_GENERATOR.generate());

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                dataType = JooqUtil.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (domainType.isReferenceField(dataEntry.getKey())) {
                    String fieldName = getSqlCustomizer().getColumnName(
                            domainType, dataEntry.getKey() + StringUtils.capitalize(IdField.ID_FIELD_NAME));

                    if (dataEntry.getValue() instanceof Map) {
                        insertSetMoreStep.set(
                                field(fieldName, dataType),
                                getReferenceIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainType));
                    } else {
                        insertSetMoreStep.set(field(fieldName, dataType), dataEntry.getValue());
                    }
                } else {
                    insertSetMoreStep.set(
                            field(getSqlCustomizer().getColumnName(domainType, dataEntry.getKey()), dataType),
                            dataEntry.getValue());
                }
            }

            InsertFinalStep insertFinalStep = (InsertFinalStep) insertSetStep;

            sqlSaveQuery.setSql(insertFinalStep.getSQL());
            sqlSaveQuery.setSqlParameters(insertFinalStep.getBindValues());
        } else if (saveQuery instanceof UpdateQuery) {
            UpdateSetStep updateSetStep = getDslContext()
                    .update(table(getSqlCustomizer().getTableName(saveQuery.getDomainTypeName())));

            Unique uniqueKey = getUniqueKey(domainType, data);

            Condition condition = DSL.trueCondition();

            if (saveQuery.getAccessDecisionConditions() != null) {
                condition = condition.and(
                        JooqUtil.getCondition(
                                saveQuery.getDomainTypeName(),
                                saveQuery.getAccessDecisionConditions(), null, null,
                                getSqlCustomizer()));
            }

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                DataType dataType = JooqUtil.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (uniqueKey.getFields().contains(dataEntry.getKey())) {
                    condition = condition.and(field(dataEntry.getKey()).eq(dataEntry.getValue()));

                    condition = condition.and(
                            field(getSqlCustomizer().getColumnName(domainType, dataEntry.getKey()), dataType)
                                    .eq(dataEntry.getValue()));
                } else {
                    if (domainType.isReferenceField(dataEntry.getKey())) {
                        String fieldName = getSqlCustomizer().getColumnName(
                                domainType, dataEntry.getKey() + StringUtils.capitalize(IdField.ID_FIELD_NAME));

                        if (dataEntry.getValue() instanceof Map) {
                            updateSetStep.set(
                                    field(fieldName, dataType),
                                    getReferenceIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainType));
                        } else {
                            updateSetStep.set(field(fieldName, dataType), dataEntry.getValue());
                        }
                    } else {
                        updateSetStep.set(
                                field(getSqlCustomizer().getColumnName(domainType, dataEntry.getKey()), dataType),
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

    private Object getReferenceIdSelect(String fieldName, Object fieldValue, DomainType domainType) {
        Condition condition = DSL.trueCondition();

        for (Map.Entry<String, Object> fieldValueEntry : ((Map<String, Object>) fieldValue).entrySet()) {
            condition = condition.and(field(fieldValueEntry.getKey()).eq(fieldValueEntry.getValue()));
        }

        return getDslContext()
                .select(
                        field("id"))
                .from(table(getSqlCustomizer().getTableName(new TypeName(domainType.getSchema(), fieldName))))
                .where(condition).asField();
    }

    private Unique getUniqueKey(DomainType domainType, Map<String, Object> data) {
        for (Unique uniqueKey : domainType.getUniques()) {
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
        List<Condition> conditions = new ArrayList<>();

        conditions.add(
                JooqUtil.getCondition(
                        deleteQuery.getDomainTypeName(),
                        deleteQuery.getConditions(), null, null,
                        getSqlCustomizer()));

        if (deleteQuery.getAccessDecisionConditions() != null) {
            conditions.add(
                    JooqUtil.getCondition(
                            deleteQuery.getDomainTypeName(),
                            deleteQuery.getAccessDecisionConditions(), null, null,
                            getSqlCustomizer()));
        }

        DeleteFinalStep deleteFinalStep = getDslContext()
                .deleteFrom(table(getSqlCustomizer().getTableName(deleteQuery.getDomainTypeName())))
                .where(conditions);

        InlineSqlDeleteQuery sqlDeleteQuery = new InlineSqlDeleteQuery();
        sqlDeleteQuery.setSql(deleteFinalStep.getSQL());
        sqlDeleteQuery.setSqlParameters(deleteFinalStep.getBindValues());

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            logger.info("SqlDeleteQuery:\n" + sqlDeleteQuery.toString());
        }

        return sqlDeleteQuery;
    }

}
