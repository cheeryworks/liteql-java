package org.cheeryworks.liteql.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.event.AfterCreateEvent;
import org.cheeryworks.liteql.model.query.event.AfterDeleteEvent;
import org.cheeryworks.liteql.model.query.event.AfterReadEvent;
import org.cheeryworks.liteql.model.query.event.AfterUpdateEvent;
import org.cheeryworks.liteql.model.query.event.BeforeCreateEvent;
import org.cheeryworks.liteql.model.query.event.BeforeDeleteEvent;
import org.cheeryworks.liteql.model.query.event.BeforeUpdateEvent;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.query.read.SingleReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.model.query.read.result.PageReadResults;
import org.cheeryworks.liteql.model.query.read.result.ReadResult;
import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.SaveQueries;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.query.diagnostic.SaveQueryDiagnostic;
import org.cheeryworks.liteql.service.util.SqlQueryServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSqlQueryService implements QueryService {

    private static Logger logger = LoggerFactory.getLogger(AbstractSqlQueryService.class);

    private Repository repository;

    private ObjectMapper objectMapper;

    private SqlQueryParser sqlQueryParser;

    private SqlQueryExecutor sqlQueryExecutor;

    private AuditingService auditingService;

    private ApplicationEventPublisher applicationEventPublisher;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setSqlQueryParser(SqlQueryParser sqlQueryParser) {
        this.sqlQueryParser = sqlQueryParser;
    }

    public void setSqlQueryExecutor(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public AbstractSqlQueryService(
            Repository repository, ObjectMapper objectMapper,
            SqlQueryParser sqlQueryParser, SqlQueryExecutor sqlQueryExecutor,
            AuditingService auditingService,
            ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.sqlQueryParser = sqlQueryParser;
        this.sqlQueryExecutor = sqlQueryExecutor;
        this.auditingService = auditingService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public ReadResults read(QueryContext queryContext, ReadQuery readQuery) {
        return (ReadResults) query(queryContext, readQuery);
    }

    @Override
    public ReadResult read(QueryContext queryContext, SingleReadQuery singleReadQuery) {
        return (ReadResult) query(queryContext, singleReadQuery);
    }

    @Override
    public TreeReadResults read(QueryContext queryContext, TreeReadQuery treeReadQuery) {
        return (TreeReadResults) query(queryContext, treeReadQuery);
    }

    @Override
    public PageReadResults read(QueryContext queryContext, PageReadQuery pageReadQuery) {
        return (PageReadResults) query(queryContext, pageReadQuery);
    }

    private Object query(QueryContext queryContext, AbstractTypedReadQuery readQuery) {
        ReadResults results = internalRead(queryContext, readQuery);

        query(queryContext, readQuery.getAssociations(), results);

        if (readQuery instanceof SingleReadQuery) {
            return results.get(0);
        } else if (readQuery instanceof TreeReadQuery) {
            return SqlQueryServiceUtil.transformInTree(
                    results, ((TreeReadQuery) readQuery).getExpandLevel());
        } else if (readQuery instanceof PageReadQuery) {
            return new PageReadResults(
                    results,
                    ((PageReadQuery) readQuery).getPage(),
                    ((PageReadQuery) readQuery).getSize(),
                    results.getTotal());
        }

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
        normalize(readQuery.getConditions(), queryContext);

        SqlReadQuery sqlReadQuery = sqlQueryParser.getSqlReadQuery(readQuery);

        ReadResults results = getResults(sqlReadQuery);

        applicationEventPublisher.publishEvent(
                new AfterReadEvent(
                        results.getData().stream().collect(Collectors.toList()),
                        readQuery.getDomainTypeName()));

        if (readQuery instanceof PageReadQuery) {
            return new ReadResults(results, getTotal(sqlReadQuery));
        }

        return new ReadResults(results);
    }

    private long getTotal(SqlReadQuery sqlReadQuery) {
        return sqlQueryExecutor.count(
                sqlReadQuery.getTotalSql(), ((List) sqlReadQuery.getTotalSqlParameters()).toArray());
    }

    private ReadResults getResults(SqlReadQuery sqlReadQuery) {
        return sqlQueryExecutor.read(
                sqlReadQuery.getSql(), sqlReadQuery.getFields(),
                ((List) sqlReadQuery.getSqlParameters()).toArray());
    }

    @Override
    public CreateQuery create(QueryContext queryContext, CreateQuery createQuery) {
        return (CreateQuery) save(queryContext, Collections.singletonList(createQuery)).get(0);
    }

    @Override
    public UpdateQuery update(QueryContext queryContext, UpdateQuery updateQuery) {
        return (UpdateQuery) save(queryContext, Collections.singletonList(updateQuery)).get(0);
    }

    @Override
    public AbstractSaveQuery save(QueryContext queryContext, AbstractSaveQuery saveQuery) {
        return save(queryContext, Collections.singletonList(saveQuery)).get(0);
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
                publishBeforeSaveEvent(saveQueriesWithType));

        long[] persistResults = persist(saveQueriesWithType);

        saveQueryDiagnostic.setPrePersistDuration(persistResults[0]);
        saveQueryDiagnostic.setPersistDuration(persistResults[1]);
        saveQueryDiagnostic.setPersistCount(persistResults[2]);

        saveQueryDiagnostic.setAfterSaveEventProcessingDuration(
                publishAfterSaveEvent(saveQueriesWithType));

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
                    auditingService.auditingDomainObject(
                            saveQuery.getData(), repository.getDomainType(saveQuery.getDomainTypeName()),
                            queryContext.getUser());
                } else {
                    auditingService.auditingExistedDomainObject(
                            saveQuery.getData(), repository.getDomainType(saveQuery.getDomainTypeName()),
                            queryContext.getUser());
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
                    repository.getDomainType(saveQueriesWithTypeEntry.getKey()));

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

    private long publishBeforeSaveEvent(Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        return publishSaveEvent(saveQueriesWithType, true);
    }

    private long publishAfterSaveEvent(Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        return publishSaveEvent(saveQueriesWithType, false);
    }

    private long publishSaveEvent(Map<TypeName, List<AbstractSaveQuery>> saveQueriesWithType, boolean before) {
        long currentTime = System.currentTimeMillis();

        for (List<AbstractSaveQuery> saveQueries : saveQueriesWithType.values()) {
            Map<TypeName, List<Map<String, Object>>> persistDataSet = new HashMap<>();
            Map<TypeName, List<Map<String, Object>>> updateDataSet = new HashMap<>();

            separateDataSet(saveQueries, persistDataSet, updateDataSet);

            for (Map.Entry<TypeName, List<Map<String, Object>>> dataSetEntry : persistDataSet.entrySet()) {
                if (before) {
                    applicationEventPublisher.publishEvent(
                            new BeforeCreateEvent(dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Create));
                } else {
                    applicationEventPublisher.publishEvent(
                            new AfterCreateEvent(dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Create));
                }
            }

            for (Map.Entry<TypeName, List<Map<String, Object>>> dataSetEntry : updateDataSet.entrySet()) {
                if (before) {
                    applicationEventPublisher.publishEvent(
                            new BeforeUpdateEvent(dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Update));
                } else {
                    applicationEventPublisher.publishEvent(
                            new AfterUpdateEvent(dataSetEntry.getValue(), dataSetEntry.getKey(), QueryType.Update));
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
                SqlSaveQuery sqlSaveQuery = sqlQueryParser.getSqlSaveQuery(
                        saveQuery, repository.getDomainType(saveQueriesWithTypeEntry.getKey()));

                if (sql == null) {
                    sql = sqlSaveQuery.getSql();
                }

                parametersList.add(((List) sqlSaveQuery.getSqlParameters()).toArray());
            }

            sqlQueryExecutor.executeBatch(sql, parametersList);

            int i = 0;

            for (AbstractSaveQuery saveQuery : saveQueriesWithTypeEntry.getValue()) {
                if (saveQuery instanceof CreateQuery) {
                    saveQuery.getData().put("id", parametersList.get(i)[0]);
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
        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
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
        normalize(deleteQuery.getConditions(), queryContext);

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
                    && IdField.ID_FIELD_NAME.equals(
                    deleteQuery.getConditions().get(0).getField())
                    && ConditionClause.EQUALS.equals(deleteQuery.getConditions().get(0).getCondition())
                    && ConditionType.String.equals(deleteQuery.getConditions().get(0).getType())) {
                Map<String, Object> result = new HashMap<>();
                result.put(
                        IdField.ID_FIELD_NAME,
                        deleteQuery.getConditions().get(0).getValue());

                results = new ReadResults(Collections.singletonList(new ReadResult(result)));
            }

            if (CollectionUtils.isNotEmpty(results)) {
                applicationEventPublisher.publishEvent(
                        new BeforeDeleteEvent(
                                results.getData().stream().collect(Collectors.toList()),
                                deleteQuery.getDomainTypeName(), QueryType.Delete));
            }
        }

        SqlDeleteQuery sqlDeleteQuery = sqlQueryParser.getSqlDeleteQuery(deleteQuery);

        int rows = sqlQueryExecutor.execute(
                sqlDeleteQuery.getSql(), ((List) sqlDeleteQuery.getSqlParameters()).toArray());

        if (!deleteQuery.isTruncated()) {
            if (CollectionUtils.isNotEmpty(results)) {
                applicationEventPublisher.publishEvent(
                        new AfterDeleteEvent(
                                results.getData().stream().collect(Collectors.toList()),
                                deleteQuery.getDomainTypeName(), QueryType.Delete));
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
        if (query instanceof AbstractTypedReadQuery) {
            AbstractTypedReadQuery readQuery = (AbstractTypedReadQuery) query;

            return query(queryContext, readQuery);
        } else if (query instanceof AbstractSaveQuery) {
            AbstractSaveQuery saveQuery = (AbstractSaveQuery) query;

            return save(queryContext, saveQuery);
        } else if (query instanceof SaveQueries) {
            SaveQueries saveQueries = (SaveQueries) query;

            return save(queryContext, saveQueries);
        } else if (query instanceof DeleteQuery) {
            DeleteQuery deleteQuery = (DeleteQuery) query;

            return delete(queryContext, deleteQuery);
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

        throw new UnsupportedOperationException(LiteQLJsonUtil.toJson(this.objectMapper, query));
    }

    private void normalize(List<QueryCondition> conditions, QueryContext queryContext) {
        if (conditions != null) {
            for (QueryCondition condition : conditions) {
                if (condition.getValue() != null && condition.getValue().equals("$userId")) {
                    condition.setValue(queryContext.getUser().getId());
                }

                if (condition.getConditions() != null) {
                    normalize(condition.getConditions(), queryContext);
                }
            }
        }
    }
}
