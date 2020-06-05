package org.cheeryworks.liteql.sql.jooq;

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
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.SaveQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.index.Unique;
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
import org.jooq.Condition;
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
    public SqlReadQuery getSqlReadQuery(AbstractTypedReadQuery readQuery) {
        SqlReadQuery sqlReadQuery = new InlineSqlReadQuery();

        Condition condition = getCondition(readQuery.getConditions(), null, TABLE_ALIAS_PREFIX);

        List<org.jooq.Field<Object>> fields = getSelectFields(
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
            List<QuerySort> querySorts = readQuery.getSorts();

            for (QuerySort querySort : querySorts) {
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
            List<JoinedReadQuery> joinedReadQueries, String joinedTableAliasPrefix, SqlReadQuery sqlReadQuery) {
        List<JoinedTable> joinedTables = new ArrayList<JoinedTable>();

        if (CollectionUtils.isNotEmpty(joinedReadQueries)) {
            for (JoinedReadQuery joinedReadQuery : joinedReadQueries) {
                JoinedTable joinedTable = new JoinedTable();
                joinedTable.setTableName(getTableName(joinedReadQuery.getDomainType()));
                joinedTable.setTableAlias(joinedTableAliasPrefix + joinedTables.size());
                joinedTable.setFields(
                        getSelectFields(
                                joinedReadQuery.getDomainType(), joinedReadQuery.getFields(),
                                joinedTable.getTableAlias(), sqlReadQuery));

                QueryConditions joinConditions = new QueryConditions();
                joinConditions.add(new QueryCondition(
                        Field.ID_FIELD_NAME,
                        ConditionClause.EQUALS, ConditionType.Field,
                        getDomainType(joinedReadQuery.getDomainType()).getName().split("\\.")[1]
                                + StringUtils.capitalize(Field.ID_FIELD_NAME)));
                joinedTable.setJoinCondition(
                        getCondition(
                                joinConditions,
                                joinedTableAliasPrefix, joinedTable.getTableAlias()));

                joinedTable.setCondition(
                        getCondition(joinedReadQuery.getConditions(), null, joinedTable.getTableAlias()));

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
            String domainTypeName, FieldDefinitions fieldDefinitions,
            String tableAlias, SqlReadQuery sqlReadQuery) {
        List<org.jooq.Field<Object>> fields = new ArrayList<>();

        if (MapUtils.isEmpty(sqlReadQuery.getFields())) {
            sqlReadQuery.setFields(new HashMap<>());
        }

        DomainType domainType = getDomainType(domainTypeName);

        if (TABLE_ALIAS_PREFIX.equals(tableAlias) && fieldDefinitions == null) {
            for (Field field : domainType.getFields()) {
                if (field instanceof ReferenceField || field instanceof IdField) {
                    continue;
                } else {
                    fields.add(
                            field(tableAlias + "." + SqlQueryServiceUtil.getColumnNameByFieldName(field.getName())));
                    sqlReadQuery.getFields().put(
                            SqlQueryServiceUtil.getColumnNameByFieldName(field.getName()), field);
                }
            }
        } else if (fieldDefinitions != null) {
            for (FieldDefinition fieldDefinition : fieldDefinitions) {
                fields.add(
                        field(tableAlias + "."
                                + SqlQueryServiceUtil.getColumnNameByFieldName(fieldDefinition.getName()))
                                .as(SqlQueryServiceUtil.getColumnNameByFieldName(fieldDefinition.getAlias())));

                for (Field field : domainType.getFields()) {
                    if (field.getName().equals(fieldDefinition.getName())) {
                        sqlReadQuery.getFields().put(
                                SqlQueryServiceUtil.getColumnNameByFieldName(fieldDefinition.getAlias()), field);
                    }
                }
            }
        }

        return fields;
    }

    private DomainType getDomainType(String domainTypeName) {
        return getRepository().getDomainType(domainTypeName);
    }

    private Condition getCondition(QueryConditions conditions, String parentTableAlias, String tableAlias) {
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
                    fieldDefinitions.get(Field.ID_FIELD_NAME));

            InsertSetMoreStep insertSetMoreStep = insertSetStep.set(
                    field("id", dataType),
                    UUID_GENERATOR.generate());

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                dataType = jooqDataType.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (domainType.isReferenceField(dataEntry.getKey())) {
                    String fieldName = SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()
                            + StringUtils.capitalize(Field.ID_FIELD_NAME));

                    if (dataEntry.getValue() instanceof Map) {
                        insertSetMoreStep.set(
                                field(fieldName, dataType),
                                getReferenceIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainType));
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

            Unique uniqueKey = getUniqueKey(domainType, data);

            Condition condition = DSL.trueCondition();

            for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
                DataType dataType = jooqDataType.getDataType(fieldDefinitions.get(dataEntry.getKey()));

                if (uniqueKey.getFields().contains(dataEntry.getKey())) {
                    condition = condition.and(field(dataEntry.getKey()).eq(dataEntry.getValue()));

                    condition = condition.and(
                            field(SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()), dataType)
                                    .eq(dataEntry.getValue()));
                } else {
                    if (domainType.isReferenceField(dataEntry.getKey())) {
                        String fieldName = SqlQueryServiceUtil.getColumnNameByFieldName(dataEntry.getKey()
                                + StringUtils.capitalize(Field.ID_FIELD_NAME));

                        if (dataEntry.getValue() instanceof Map) {
                            updateSetStep.set(
                                    field(fieldName, dataType),
                                    getReferenceIdSelect(dataEntry.getKey(), dataEntry.getValue(), domainType));
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

    private Object getReferenceIdSelect(String fieldName, Object fieldValue, DomainType domainType) {
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
