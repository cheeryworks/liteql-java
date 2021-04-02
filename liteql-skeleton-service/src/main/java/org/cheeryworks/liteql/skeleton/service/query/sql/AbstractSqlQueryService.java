package org.cheeryworks.liteql.skeleton.service.query.sql;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryEventPublisher;
import org.cheeryworks.liteql.skeleton.event.publisher.query.QueryPublisher;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.Queries;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.diagnostic.SaveQueryDiagnostic;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionClause;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.event.AbstractListMapQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterCreateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterDeleteQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterReadQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.AfterUpdateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.BeforeCreateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.BeforeDeleteQueryEvent;
import org.cheeryworks.liteql.skeleton.query.event.BeforeUpdateQueryEvent;
import org.cheeryworks.liteql.skeleton.query.exception.UnsupportedQueryException;
import org.cheeryworks.liteql.skeleton.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.SingleReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.TreeReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.page.Page;
import org.cheeryworks.liteql.skeleton.query.read.result.PageReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResult;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.TypedPageReadResults;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.SaveQueries;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;
import org.cheeryworks.liteql.skeleton.schema.DomainType;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;
import org.cheeryworks.liteql.skeleton.service.query.QueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.skeleton.sql.SqlDeleteQuery;
import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;
import org.cheeryworks.liteql.skeleton.sql.SqlSaveQuery;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.skeleton.util.SqlQueryServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSqlQueryService extends AbstractSqlService implements SqlQueryService {

    private static Logger logger = LoggerFactory.getLogger(AbstractSqlQueryService.class);

    private SqlQueryParser sqlQueryParser;

    private SqlQueryExecutor sqlQueryExecutor;

    private QueryAuditingService queryAuditingService;

    private QueryPublisher queryPublisher;

    private QueryEventPublisher queryEventPublisher;

    public AbstractSqlQueryService(
            LiteQLProperties liteQLProperties,
            SqlQueryParser sqlQueryParser,
            SqlQueryExecutor sqlQueryExecutor,
            QueryAuditingService queryAuditingService,
            QueryPublisher queryPublisher,
            QueryEventPublisher queryEventPublisher) {
        super(liteQLProperties);
        this.sqlQueryParser = sqlQueryParser;
        this.sqlQueryExecutor = sqlQueryExecutor;
        this.queryAuditingService = queryAuditingService;
        this.queryPublisher = queryPublisher;
        this.queryEventPublisher = queryEventPublisher;
    }

    @Override
    public <T extends DomainType> List<T> read(
            QueryContext queryContext, ReadQuery readQuery, Class<T> domainType) {

        DomainTypeDefinition domainTypeDefinition = this.sqlQueryParser.getSchemaService()
                .getDomainTypeDefinition(LiteQL.SchemaUtils.getTypeName(domainType));

        return SqlQueryServiceUtil.getTypedResults(read(queryContext, readQuery), domainType, domainTypeDefinition);
    }

    @Override
    public ReadResults read(QueryContext queryContext, ReadQuery readQuery) {
        return readQuery.getResult(query(queryContext, readQuery));
    }

    @Override
    public <T extends DomainType> T read(
            QueryContext queryContext, SingleReadQuery singleReadQuery, Class<T> domainType) {
        ReadResult readResult = read(queryContext, singleReadQuery);

        DomainTypeDefinition domainTypeDefinition = this.sqlQueryParser.getSchemaService()
                .getDomainTypeDefinition(LiteQL.SchemaUtils.getTypeName(domainType));

        if (readResult != null) {
            return SqlQueryServiceUtil.getTypedResult(readResult, domainType, domainTypeDefinition);
        }

        return null;
    }

    @Override
    public ReadResult read(QueryContext queryContext, SingleReadQuery singleReadQuery) {
        return singleReadQuery.getResult(query(queryContext, singleReadQuery));
    }

    @Override
    public TreeReadResults read(QueryContext queryContext, TreeReadQuery treeReadQuery) {
        return treeReadQuery.getResult(query(queryContext, treeReadQuery));
    }

    @Override
    public <T extends DomainType> Page<T> read(
            QueryContext queryContext, PageReadQuery pageReadQuery, Class<T> domainType) {
        PageReadResults pageReadResults = read(queryContext, pageReadQuery);

        List<T> results = new ArrayList<>();

        DomainTypeDefinition domainTypeDefinition = this.sqlQueryParser.getSchemaService()
                .getDomainTypeDefinition(LiteQL.SchemaUtils.getTypeName(domainType));

        for (ReadResult readResult : pageReadResults.getData()) {
            results.add(SqlQueryServiceUtil.getTypedResult(readResult, domainType, domainTypeDefinition));
        }

        return new TypedPageReadResults<>(
                results, pageReadResults.getPage(), pageReadResults.getSize(), pageReadResults.getTotal());
    }

    @Override
    public PageReadResults read(QueryContext queryContext, PageReadQuery pageReadQuery) {
        return pageReadQuery.getResult(query(queryContext, pageReadQuery));
    }

    private <T extends AbstractTypedReadQuery> ReadResults query(QueryContext queryContext, T readQuery) {
        ReadResults results = internalRead(queryContext, readQuery);

        query(queryContext, readQuery.getAssociations(), results);

        return results;
    }

    private void query(QueryContext queryContext, List<ReadQuery> readQueries, ReadResults results) {
        if (CollectionUtils.isNotEmpty(readQueries)) {
            for (ReadQuery readQuery : readQueries) {
                if (readQuery.getReferences() == null) {
                    throw new IllegalArgumentException("References not defined");
                }

                ReadResults associatedResults = internalRead(queryContext, readQuery);

                for (Map<String, Object> result : results) {
                    for (Map<String, Object> associatedResult : associatedResults) {
                        boolean matched = true;

                        for (Map.Entry<String, String> reference : readQuery.getReferences().entrySet()) {
                            if (!associatedResult.get(reference.getKey())
                                    .equals(result.get(reference.getValue()))) {
                                matched = false;
                            }
                        }

                        if (matched) {
                            result.putAll(associatedResult);
                        }
                    }
                }

                query(queryContext, readQuery.getAssociations(), results);
            }
        }
    }

    private ReadResults internalRead(QueryContext queryContext, AbstractTypedReadQuery readQuery) {
        if (readQuery instanceof PublicQuery) {
            publishQuery((PublicQuery) readQuery);
        }

        SqlReadQuery sqlReadQuery = sqlQueryParser.getSqlReadQuery(readQuery);

        ReadResults results = getResults(sqlReadQuery);

        publishQueryEvent(
                new AfterReadQueryEvent(
                        results.getData().stream().collect(Collectors.toList()),
                        readQuery.getDomainTypeName(), readQuery.getQueryType(), queryContext, readQuery.getScope()));

        if (readQuery instanceof PageReadQuery) {
            return new ReadResults(results, getTotal(sqlReadQuery));
        }

        return new ReadResults(results);
    }

    private long getTotal(SqlReadQuery sqlReadQuery) {
        return sqlQueryExecutor.count(
                sqlReadQuery.getTotalSql(), sqlReadQuery.getTotalSqlParameters());
    }

    private ReadResults getResults(SqlReadQuery sqlReadQuery) {
        return sqlQueryExecutor.read(sqlReadQuery);
    }

    @Override
    public <T extends DomainType> T create(QueryContext queryContext, T domainEntity) {
        return save(queryContext, domainEntity, QueryType.Create);
    }

    @Override
    public <T extends DomainType> List<T> create(QueryContext queryContext, Collection<T> domainEntities) {
        return save(queryContext, domainEntities, QueryType.Create);
    }

    @Override
    public CreateQuery create(QueryContext queryContext, CreateQuery createQuery) {
        return (CreateQuery) save(queryContext, Collections.singletonList(createQuery)).get(0);
    }

    @Override
    public <T extends DomainType> T update(QueryContext queryContext, T domainEntity) {
        return save(queryContext, domainEntity, QueryType.Update);
    }

    @Override
    public <T extends DomainType> List<T> update(QueryContext queryContext, Collection<T> domainEntities) {
        return save(queryContext, domainEntities, QueryType.Update);
    }

    @Override
    public UpdateQuery update(QueryContext queryContext, UpdateQuery updateQuery) {
        return (UpdateQuery) save(queryContext, Collections.singletonList(updateQuery)).get(0);
    }

    @Override
    public AbstractSaveQuery save(QueryContext queryContext, AbstractSaveQuery saveQuery) {
        return save(queryContext, Collections.singletonList(saveQuery)).get(0);
    }

    private <T extends DomainType> T save(QueryContext queryContext, T domainEntity, QueryType queryType) {
        try {
            Class<T> domainType = (Class<T>) domainEntity.getClass();

            DomainTypeDefinition domainTypeDefinition = this.sqlQueryParser.getSchemaService()
                    .getDomainTypeDefinition(LiteQL.SchemaUtils.getTypeName(domainType));

            AbstractSaveQuery saveQuery = SqlQueryServiceUtil.transformObjectToSaveQuery(
                    domainEntity, queryType, domainTypeDefinition);

            if (QueryType.Create.equals(queryType)) {
                saveQuery = create(queryContext, (CreateQuery) saveQuery);
            } else if (QueryType.Update.equals(queryType)) {
                saveQuery = update(queryContext, (UpdateQuery) saveQuery);
            }

            return SqlQueryServiceUtil.getTypedResult(saveQuery.getData(), domainType, domainTypeDefinition);
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable.getMessage(), throwable);
        }
    }

    private <T extends DomainType> List<T> save(
            QueryContext queryContext, Collection<T> entities, QueryType queryType) {
        List<T> results = new ArrayList<>();

        if (CollectionUtils.isEmpty(entities)) {
            return results;
        }

        List<AbstractSaveQuery> saveQueries = new ArrayList<>();

        Class<T> domainType = (Class<T>) entities.stream().findFirst().get().getClass();

        DomainTypeDefinition domainTypeDefinition = this.sqlQueryParser.getSchemaService()
                .getDomainTypeDefinition(LiteQL.SchemaUtils.getTypeName(domainType));

        for (T entity : entities) {
            AbstractSaveQuery saveQuery
                    = SqlQueryServiceUtil.transformObjectToSaveQuery(entity, queryType, domainTypeDefinition);

            saveQueries.add(saveQuery);
        }

        saveQueries = save(queryContext, saveQueries);

        for (AbstractSaveQuery saveQuery : saveQueries) {
            results.add(SqlQueryServiceUtil.getTypedResult(saveQuery.getData(), domainType, domainTypeDefinition));
        }

        return results;
    }

    @Override
    public List<AbstractSaveQuery> save(QueryContext queryContext, List<AbstractSaveQuery> saveQueries) {
        long currentTime = System.currentTimeMillis();
        SaveQueryDiagnostic saveQueryDiagnostic = new SaveQueryDiagnostic();

        Map<Integer, Map<TypeName, List<AbstractSaveQuery>>> sortedSaveQueries = new HashMap<>();

        saveQueryDiagnostic.setTransformingSaveQueryDuration(
                transformingSaveQuery(sortedSaveQueries, saveQueries, 0));

        for (int i = 0; i < sortedSaveQueries.size(); i++) {
            SaveQueryDiagnostic batchSaveDiagnostic = batchSave(queryContext, sortedSaveQueries.get(i), i);

            saveQueryDiagnostic.add(batchSaveDiagnostic);
        }

        printDiagnosticMessages(saveQueryDiagnostic, System.currentTimeMillis() - currentTime, null, null);

        return saveQueries;
    }

    private long transformingSaveQuery(
            Map<Integer, Map<TypeName, List<AbstractSaveQuery>>> sortedSaveQueries,
            List<AbstractSaveQuery> saveQueries, int level) {
        long currentTime = System.currentTimeMillis();
        long duration = 0;

        Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType = sortedSaveQueries.get(level);

        if (saveQueriesWithType == null) {
            saveQueriesWithType = new HashMap<>();
            sortedSaveQueries.put(level, saveQueriesWithType);
        }

        for (AbstractSaveQuery saveQuery : saveQueries) {
            transformingFieldValue(saveQuery.getData());

            List<AbstractSaveQuery> saveQueriesInSameType = saveQueriesWithType.get(saveQuery.getDomainTypeName());

            if (saveQueriesInSameType == null) {
                saveQueriesInSameType = new ArrayList<>();
                saveQueriesWithType.put(saveQuery.getDomainTypeName(), saveQueriesInSameType);
            }

            saveQueriesInSameType.add(saveQuery);

            duration = System.currentTimeMillis() - currentTime;

            if (CollectionUtils.isNotEmpty(saveQuery.getAssociations())) {
                duration += transformingSaveQuery(sortedSaveQueries, saveQuery.getAssociations(), level + 1);
            }
        }

        return duration;
    }

    private void transformingFieldValue(Map<String, Object> data) {
        for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
            if (dataEntry.getValue() != null) {
                if (dataEntry.getValue() != null && dataEntry.getValue() instanceof Date) {
                    dataEntry.setValue(new Timestamp(((Date) dataEntry.getValue()).getTime()));
                }
            }
        }
    }

    private SaveQueryDiagnostic batchSave(
            QueryContext queryContext, Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType, int i) {
        long currentTime = System.currentTimeMillis();

        SaveQueryDiagnostic saveQueryDiagnostic = new SaveQueryDiagnostic();

        saveQueryDiagnostic.setAuditingEntitiesDuration(auditingEntities(queryContext, saveQueriesWithType));

        saveQueryDiagnostic.setBeforeSaveEventProcessingDuration(
                publishBeforeSaveEvent(queryContext, saveQueriesWithType));

        long[] persistResults = persist(saveQueriesWithType);

        saveQueryDiagnostic.setPrePersistDuration(persistResults[0]);
        saveQueryDiagnostic.setPersistDuration(persistResults[1]);
        saveQueryDiagnostic.setPersistCount(persistResults[2]);

        saveQueryDiagnostic.setAfterSaveEventProcessingDuration(
                publishAfterSaveEvent(queryContext, saveQueriesWithType));

        saveQueryDiagnostic.setLinkingReferencesDuration(linkingParent(saveQueriesWithType));

        printDiagnosticMessages(
                saveQueryDiagnostic, System.currentTimeMillis() - currentTime, i, saveQueriesWithType.keySet());

        return saveQueryDiagnostic;
    }

    private long auditingEntities(
            QueryContext queryContext, Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        long currentTime = System.currentTimeMillis();
        for (List<AbstractSaveQuery> saveQueries : saveQueriesWithType.values()) {
            for (AbstractSaveQuery saveQuery : saveQueries) {
                if (saveQuery instanceof CreateQuery) {
                    queryAuditingService.auditingDomainObject(
                            saveQuery.getData(),
                            this.sqlQueryParser.getSchemaService().getDomainTypeDefinition(
                                    saveQuery.getDomainTypeName()),
                            queryContext);
                } else {
                    queryAuditingService.auditingExistedDomainObject(
                            saveQuery.getData(),
                            this.sqlQueryParser.getSchemaService().getDomainTypeDefinition(
                                    saveQuery.getDomainTypeName()),
                            queryContext);
                }
            }
        }
        return System.currentTimeMillis() - currentTime;
    }

    private long linkingParent(Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<TypeName, List<AbstractSaveQuery>> saveQueriesWithTypeEntry
                : saveQueriesWithType.entrySet()) {
            List<AbstractSaveQuery> saveQueries = saveQueriesWithTypeEntry.getValue();
            Map<String, Class> domainFieldsInMap = SqlQueryServiceUtil.getFieldDefinitions(
                    this.sqlQueryParser.getSchemaService().getDomainTypeDefinition(
                            saveQueriesWithTypeEntry.getKey()));

            for (AbstractSaveQuery saveQuery : saveQueries) {
                if (CollectionUtils.isNotEmpty(saveQuery.getAssociations())) {
                    for (AbstractSaveQuery associatedSaveQuery : saveQuery.getAssociations()) {
                        if (associatedSaveQuery.getReferences() != null) {
                            Map<String, String> references = associatedSaveQuery.getReferences();

                            for (Map.Entry<String, String> reference : references.entrySet()) {
                                try {
                                    Object fieldValue = saveQuery.getData().get(reference.getValue());

                                    if (fieldValue != null
                                            && associatedSaveQuery.getData().get(reference.getKey()) == null) {
                                        Class fieldType = domainFieldsInMap.get(reference.getKey());

                                        if (fieldType != null && fieldType.equals(fieldValue.getClass())) {
                                            associatedSaveQuery.getData().put(
                                                    reference.getKey(),
                                                    ConvertUtils.convert(fieldValue, fieldType));
                                        } else {
                                            associatedSaveQuery.getData().put(reference.getKey(), fieldValue);
                                        }
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        }
                    }
                }
            }
        }

        return System.currentTimeMillis() - currentTime;
    }

    private long publishBeforeSaveEvent(
            QueryContext queryContext, Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        return publishSaveEvent(queryContext, saveQueriesWithType, true);
    }

    private long publishAfterSaveEvent(
            QueryContext queryContext, Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        return publishSaveEvent(queryContext, saveQueriesWithType, false);
    }

    private long publishSaveEvent(
            QueryContext queryContext, Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType, boolean before) {
        long currentTime = System.currentTimeMillis();

        for (List<AbstractSaveQuery> saveQueries : saveQueriesWithType.values()) {
            Map<TypeName, List<Map<String, Object>>> persistDataSet = new HashMap<>();
            Map<TypeName, List<Map<String, Object>>> updateDataSet = new HashMap<>();

            separateDataSet(saveQueries, persistDataSet, updateDataSet);

            for (Map.Entry<TypeName, List<Map<String, Object>>> dataSetEntry : persistDataSet.entrySet()) {
                if (before) {
                    publishQueryEvent(
                            new BeforeCreateQueryEvent(
                                    dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Create, queryContext));
                } else {
                    publishQueryEvent(
                            new AfterCreateQueryEvent(
                                    dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Create, queryContext));
                }
            }

            for (Map.Entry<TypeName, List<Map<String, Object>>> dataSetEntry : updateDataSet.entrySet()) {
                if (before) {
                    publishQueryEvent(
                            new BeforeUpdateQueryEvent(
                                    dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Update, queryContext));
                } else {
                    publishQueryEvent(
                            new AfterUpdateQueryEvent(
                                    dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Update, queryContext));
                }
            }
        }

        return System.currentTimeMillis() - currentTime;
    }

    private void separateDataSet(
            List<AbstractSaveQuery> saveQueries,
            Map<TypeName, List<Map<String, Object>>> persistDataSet,
            Map<TypeName, List<Map<String, Object>>> updateDataSet) {
        for (AbstractSaveQuery saveQuery : saveQueries) {
            if (saveQuery instanceof CreateQuery) {
                addToDataSet(persistDataSet, saveQuery.getDomainTypeName(), saveQuery.getData());
            } else {
                addToDataSet(updateDataSet, saveQuery.getDomainTypeName(), saveQuery.getData());
            }
        }
    }

    private static <T> void addToDataSet(
            Map<TypeName, List<T>> dataSetWithKey, TypeName domainTypeName, T data) {
        List<T> dataSet = dataSetWithKey.get(domainTypeName);

        if (dataSet == null) {
            dataSet = new ArrayList<>();
            dataSetWithKey.put(domainTypeName, dataSet);
        }

        dataSet.add(data);
    }

    private long[] persist(Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        long currentTime;
        long persistDuration = 0;
        long prePersistDuration = 0;
        long count = 0;

        currentTime = System.currentTimeMillis();

        prePersistDuration += System.currentTimeMillis() - currentTime;

        currentTime = System.currentTimeMillis();

        for (Map.Entry<TypeName, List<AbstractSaveQuery>> saveQueriesWithTypeEntry
                : saveQueriesWithType.entrySet()) {
            String sql = null;

            List<Object[]> parametersList = new ArrayList<>();

            for (AbstractSaveQuery saveQuery : saveQueriesWithTypeEntry.getValue()) {
                if (saveQuery instanceof PublicQuery) {
                    publishQuery((PublicQuery) saveQuery);
                }

                SqlSaveQuery sqlSaveQuery = sqlQueryParser.getSqlSaveQuery(
                        saveQuery,
                        this.sqlQueryParser.getSchemaService().getDomainTypeDefinition(
                                saveQueriesWithTypeEntry.getKey()));

                if (sql == null) {
                    sql = sqlSaveQuery.getSql();
                }

                parametersList.add(sqlSaveQuery.getSqlParameters());
            }

            sqlQueryExecutor.executeBatch(sql, parametersList);

            int i = 0;

            for (AbstractSaveQuery saveQuery : saveQueriesWithTypeEntry.getValue()) {
                if (saveQuery instanceof CreateQuery) {
                    saveQuery.getData().put(IdField.ID_FIELD_NAME, parametersList.get(i)[0]);
                }

                i++;
            }
        }

        persistDuration += System.currentTimeMillis() - currentTime;

        return new long[]{prePersistDuration, persistDuration, count};
    }

    private void printDiagnosticMessages(
            SaveQueryDiagnostic saveQueryDiagnostic, long totalDuration,
            Integer level, Set<TypeName> domainTypeNames) {
        if (getLiteQLProperties().isDiagnosticEnabled()) {
            int messageLength = 50;
            String beginMessage = "Diagnostic Messages (Save)";

            Map<String, Object> diagnosticMessages = new LinkedHashMap<String, Object>();

            if (level != null) {
                diagnosticMessages.put("Level:", level);
            } else {
                beginMessage = "Diagnostic Summary Messages (Save)";
            }

            if (CollectionUtils.isNotEmpty(domainTypeNames)) {
                int i = 1;
                for (TypeName domainTypeName : domainTypeNames) {
                    String domainTypeFullName = domainTypeName.getFullname();
                    if (domainTypeFullName.length() > 40) {
                        domainTypeFullName = StringUtils.abbreviateMiddle(domainTypeFullName, "~", 40);
                    }

                    diagnosticMessages.put("TypeName" + i + ":", domainTypeFullName);
                    i++;
                }
            }

            logger.info(
                    StringUtils.rightPad(
                            StringUtils.leftPad(beginMessage, (messageLength / 2) + (beginMessage.length() / 2), "="),
                            messageLength,
                            "="));

            diagnosticMessages.put("Total Duration:", totalDuration);
            diagnosticMessages.put("Transform Duration:",
                    saveQueryDiagnostic.getTransformingSaveQueryDuration());
            diagnosticMessages.put("Audit Duration:",
                    saveQueryDiagnostic.getAuditingEntitiesDuration());
            diagnosticMessages.put("Linking References Duration:",
                    saveQueryDiagnostic.getLinkingReferencesDuration());
            diagnosticMessages.put(
                    "Before Save event processing Duration:",
                    saveQueryDiagnostic.getBeforeSaveEventProcessingDuration());
            diagnosticMessages.put("Pre Persistence Duration:",
                    saveQueryDiagnostic.getPrePersistDuration());
            diagnosticMessages.put("Persistence Duration:",
                    saveQueryDiagnostic.getPersistDuration());
            diagnosticMessages.put("Persistence Count:",
                    saveQueryDiagnostic.getPersistCount());
            diagnosticMessages.put("After Save event processing Duration:",
                    saveQueryDiagnostic.getAfterSaveEventProcessingDuration());
            diagnosticMessages.put("Other processing Duration:",
                    totalDuration - saveQueryDiagnostic.getTotalDuration());

            for (Map.Entry<String, Object> diagnosticMessageEntry : diagnosticMessages.entrySet()) {
                logger.info(diagnosticMessageEntry.getKey() + StringUtils.leftPad(
                        diagnosticMessageEntry.getValue().toString(),
                        messageLength - diagnosticMessageEntry.getKey().length()));
            }

            logger.info(StringUtils.repeat("=", messageLength));
        }
    }

    @Override
    public int delete(QueryContext queryContext, DeleteQuery deleteQuery) {
        publishQuery(deleteQuery);

        ReadResults results = null;

        if (!deleteQuery.isTruncated()) {
            if (CollectionUtils.isEmpty(deleteQuery.getConditions())) {
                throw new RuntimeException("For safety, delete operation requires at least one condition");
            }

            ReadQuery readQuery = new ReadQuery();
            readQuery.setDomainTypeName(deleteQuery.getDomainTypeName());
            readQuery.setConditions(deleteQuery.getConditions());

            results = read(queryContext, readQuery);

            if (CollectionUtils.isEmpty(results)
                    && deleteQuery.getConditions() != null
                    && deleteQuery.getConditions().size() == 1
                    && IdField.ID_FIELD_NAME.equals(deleteQuery.getConditions().get(0).getField())
                    && ConditionClause.EQUALS.equals(deleteQuery.getConditions().get(0).getCondition())
                    && ConditionType.String.equals(deleteQuery.getConditions().get(0).getType())) {
                Map<String, Object> result = new HashMap<>();
                result.put(
                        IdField.ID_FIELD_NAME,
                        deleteQuery.getConditions().get(0).getValue());

                results = new ReadResults(Collections.singletonList(new ReadResult(result)));
            }

            if (CollectionUtils.isNotEmpty(results)) {
                publishQueryEvent(
                        new BeforeDeleteQueryEvent(
                                results.getData().stream().collect(Collectors.toList()),
                                deleteQuery.getDomainTypeName(), QueryType.Delete, queryContext));
            }
        }

        SqlDeleteQuery sqlDeleteQuery = sqlQueryParser.getSqlDeleteQuery(deleteQuery);

        int rows = sqlQueryExecutor.execute(
                sqlDeleteQuery.getSql(), sqlDeleteQuery.getSqlParameters());

        if (!deleteQuery.isTruncated()) {
            if (CollectionUtils.isNotEmpty(results)) {
                publishQueryEvent(
                        new AfterDeleteQueryEvent(
                                results.getData().stream().collect(Collectors.toList()),
                                deleteQuery.getDomainTypeName(), QueryType.Delete, queryContext));
            }
        }

        return rows;
    }

    @Override
    public void delete(QueryContext queryContext, List<DeleteQuery> deleteQueries) {
        for (DeleteQuery deleteQuery : deleteQueries) {
            delete(queryContext, deleteQuery);
        }
    }

    @Override
    public Object execute(QueryContext queryContext, PublicQuery query) {
        if (query instanceof ReadQuery) {
            return read(queryContext, (ReadQuery) query);
        } else if (query instanceof PageReadQuery) {
            return read(queryContext, (PageReadQuery) query);
        } else if (query instanceof SingleReadQuery) {
            return query(queryContext, (SingleReadQuery) query);
        } else if (query instanceof TreeReadQuery) {
            return query(queryContext, (TreeReadQuery) query);
        } else if (query instanceof CreateQuery) {
            return create(queryContext, (CreateQuery) query);
        } else if (query instanceof UpdateQuery) {
            return update(queryContext, (UpdateQuery) query);
        } else if (query instanceof SaveQueries) {
            return save(queryContext, (SaveQueries) query);
        } else if (query instanceof DeleteQuery) {
            return delete(queryContext, (DeleteQuery) query);
        } else if (query instanceof Queries) {
            Queries queries = (Queries) query;

            Map<String, Object> results = new LinkedHashMap<>();

            Iterator<String> fieldNamesIterator = queries.keySet().iterator();

            while (fieldNamesIterator.hasNext()) {
                String fieldName = fieldNamesIterator.next();

                results.put(fieldName, execute(queryContext, queries.get(fieldName)));
            }

            return results;
        }

        throw new UnsupportedQueryException(query);
    }

    private void publishQuery(PublicQuery publicQuery) {
        if (this.queryPublisher != null) {
            this.queryPublisher.publish(publicQuery);
        }
    }

    private void publishQueryEvent(AbstractListMapQueryEvent abstractListMapQueryEvent) {
        if (this.queryEventPublisher != null) {
            this.queryEventPublisher.publish(abstractListMapQueryEvent);
        }
    }

}
