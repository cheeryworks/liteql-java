package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.query.AbstractQuery;
import org.cheeryworks.liteql.model.query.CreateQuery;
import org.cheeryworks.liteql.model.query.DeleteQuery;
import org.cheeryworks.liteql.model.query.PageReadQuery;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.ReadQuery;
import org.cheeryworks.liteql.model.query.SaveQuery;
import org.cheeryworks.liteql.model.query.SingleReadQuery;
import org.cheeryworks.liteql.model.query.TreeReadQuery;
import org.cheeryworks.liteql.model.query.UpdateQuery;
import org.cheeryworks.liteql.model.query.result.PageReadResults;
import org.cheeryworks.liteql.model.query.result.ReadResult;
import org.cheeryworks.liteql.model.query.result.ReadResults;
import org.cheeryworks.liteql.model.query.result.TreeReadResults;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.query.diagnostic.SaveQueryDiagnostic;
import org.cheeryworks.liteql.sql.util.SqlQueryServiceUtil;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SqlQueryService implements QueryService {

    private static Logger logger = LoggerFactory.getLogger(SqlQueryService.class);

    private Repository repository;

    private SqlQueryParser sqlQueryParser;

    private SqlQueryExecutor sqlQueryExecutor;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setSqlQueryParser(SqlQueryParser sqlQueryParser) {
        this.sqlQueryParser = sqlQueryParser;
    }

    public void setSqlQueryExecutor(SqlQueryExecutor sqlQueryExecutor) {
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    public SqlQueryService(
            Repository repository, SqlQueryParser sqlQueryParser, SqlQueryExecutor sqlQueryExecutor) {
        this.repository = repository;
        this.sqlQueryParser = sqlQueryParser;
        this.sqlQueryExecutor = sqlQueryExecutor;
    }

    @Override
    public ReadResults read(ReadQuery readQuery) {
        return (ReadResults) query(readQuery);
    }

    @Override
    public ReadResult read(SingleReadQuery singleReadQuery) {
        return (ReadResult) query(singleReadQuery);
    }

    @Override
    public TreeReadResults read(TreeReadQuery treeReadQuery) {
        return (TreeReadResults) query(treeReadQuery);
    }

    @Override
    public PageReadResults read(PageReadQuery pageReadQuery) {
        return (PageReadResults) query(pageReadQuery);
    }

    private Object query(ReadQuery readQuery) {
        ReadResults results = internalQuery(readQuery);

        query(readQuery.getAssociations(), results);

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

    private void query(
            List<ReadQuery> readQueries, ReadResults results) {
        if (CollectionUtils.isNotEmpty(readQueries)) {
            for (ReadQuery readQuery : readQueries) {
                if (readQuery.getReferences() == null) {
                    throw new IllegalArgumentException("References not defined");
                }

                ReadResults associatedResults = internalQuery(readQuery);

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

                query(readQuery.getAssociations(), results);
            }
        }
    }

    private ReadResults internalQuery(ReadQuery readQuery) {
        SqlReadQuery sqlReadQuery = sqlQueryParser.getSqlReadQuery(readQuery);

        ReadResults results = getResults(sqlReadQuery);

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

    private List<SaveQuery> save(List<SaveQuery> saveQueries) {
        long currentTime = System.currentTimeMillis();
        SaveQueryDiagnostic saveQueryDiagnostic = new SaveQueryDiagnostic();

        Map<Integer, Map<String, List<SaveQuery>>> sortedSaveQueries
                = new HashMap<Integer, Map<String, List<SaveQuery>>>();

        saveQueryDiagnostic.setTransformingSaveQueryDuration(
                transformingSaveQuery(sortedSaveQueries, saveQueries, 0));

        for (int i = 0; i < sortedSaveQueries.size(); i++) {
            SaveQueryDiagnostic batchSaveDiagnostic = batchSave(sortedSaveQueries.get(i), i);

            saveQueryDiagnostic.add(batchSaveDiagnostic);
        }

        printDiagnosticMessages(saveQueryDiagnostic, System.currentTimeMillis() - currentTime, null, null);

        return saveQueries;
    }

    private long transformingSaveQuery(
            Map<Integer, Map<String, List<SaveQuery>>> sortedSaveQueries, List<SaveQuery> saveQueries, int level) {
        long currentTime = System.currentTimeMillis();
        long duration = 0;

        Map<String, List<SaveQuery>> saveQueriesWithType = sortedSaveQueries.get(level);

        if (saveQueriesWithType == null) {
            saveQueriesWithType = new HashMap<String, List<SaveQuery>>();
            sortedSaveQueries.put(level, saveQueriesWithType);
        }

        for (SaveQuery saveQuery : saveQueries) {
            transformingFieldValue(saveQuery.getData());

            List<SaveQuery> saveQueriesInSameType = saveQueriesWithType.get(saveQuery.getDomainType());

            if (saveQueriesInSameType == null) {
                saveQueriesInSameType = new ArrayList<SaveQuery>();
                saveQueriesWithType.put(saveQuery.getDomainType(), saveQueriesInSameType);
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
            Map<String, List<SaveQuery>> saveQueriesWithType, int i) {
        long currentTime = System.currentTimeMillis();

        SaveQueryDiagnostic saveQueryDiagnostic = new SaveQueryDiagnostic();

        saveQueryDiagnostic.setAuditingEntitiesDuration(auditingEntities(saveQueriesWithType));

//        saveQueryDiagnostic.setBeforeSaveEventProcessingDuration(
//                publishBeforeSaveEvent(saveQueriesWithType, queryType));

        long[] persistResults = persist(saveQueriesWithType);

        saveQueryDiagnostic.setPrePersistDuration(persistResults[0]);
        saveQueryDiagnostic.setPersistDuration(persistResults[1]);
        saveQueryDiagnostic.setPostPersistDuration(persistResults[2]);
        saveQueryDiagnostic.setPersistCount(persistResults[3]);

//        saveQueryDiagnostic.setAfterSaveEventProcessingDuration(
//                publishAfterSaveEvent(saveQueriesWithType, queryType));

        saveQueryDiagnostic.setLinkingReferencesDuration(linkingParent(saveQueriesWithType));

        printDiagnosticMessages(
                saveQueryDiagnostic, System.currentTimeMillis() - currentTime, i, saveQueriesWithType.keySet());

        return saveQueryDiagnostic;
    }

    private long auditingEntities(Map<String, List<SaveQuery>> saveQueriesWithType) {
        long currentTime = System.currentTimeMillis();
//        for (List<SaveQuery> saveQueries : saveQueriesWithType.values()) {
//            for (SaveQuery saveQuery : saveQueries) {
//                if (saveQuery.isUpdating()) {
//                    entityAuditingManager.auditingExistedEntity(saveQuery.getData(), saveQuery.getTypeClass());
//                } else {
//                    entityAuditingManager.auditingEntity(saveQuery.getData(), saveQuery.getTypeClass());
//                }
//            }
//        }
        return System.currentTimeMillis() - currentTime;
    }

    private long linkingParent(Map<String, List<SaveQuery>> saveQueriesWithType) {
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, List<SaveQuery>> saveQueriesWithTypeEntry : saveQueriesWithType.entrySet()) {
            List<SaveQuery> saveQueries = saveQueriesWithTypeEntry.getValue();
            Map<String, Class> domainFieldsInMap = SqlQueryServiceUtil.getFieldDefinitions(
                    getDomainType(saveQueriesWithTypeEntry.getKey()));

            for (SaveQuery saveQuery : saveQueries) {
                if (CollectionUtils.isNotEmpty(saveQuery.getAssociations())) {
                    for (SaveQuery associatedSaveQuery : saveQuery.getAssociations()) {
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

    private long[] persist(Map<String, List<SaveQuery>> saveQueriesWithType) {
        long currentTime;
        long persistDuration = 0;
        long prePersistDuration = 0;
        long postPersistDuration = 0;
        long count = 0;

        currentTime = System.currentTimeMillis();

        prePersistDuration += System.currentTimeMillis() - currentTime;

        currentTime = System.currentTimeMillis();

        for (Map.Entry<String, List<SaveQuery>> saveQueriesWithTypeEntry : saveQueriesWithType.entrySet()) {
            String sql = null;

            List<Object[]> parametersList = new ArrayList<Object[]>();

            for (SaveQuery saveQuery : saveQueriesWithTypeEntry.getValue()) {
                SqlSaveQuery sqlSaveQuery = sqlQueryParser.getSqlSaveQuery(
                        saveQuery, getDomainType(saveQueriesWithTypeEntry.getKey()));

                if (sql == null) {
                    sql = sqlSaveQuery.getSql();
                }

                parametersList.add(((List) sqlSaveQuery.getSqlParameters()).toArray());
            }

            sqlQueryExecutor.executeBatch(sql, parametersList);

            int i = 0;

            for (SaveQuery saveQuery : saveQueriesWithTypeEntry.getValue()) {
                if (saveQuery instanceof CreateQuery) {
                    saveQuery.getData().put("id", parametersList.get(i)[0]);
                }

                i++;
            }
        }

        persistDuration += System.currentTimeMillis() - currentTime;

        currentTime = System.currentTimeMillis();
        //TODO Post Persist
        postPersistDuration += System.currentTimeMillis() - currentTime;

        return new long[]{prePersistDuration, persistDuration, postPersistDuration, count};
    }

    private DomainType getDomainType(String type) {
        String[] typeWithSchema = type.split("\\.");

        return repository.getDomainType(typeWithSchema[0], typeWithSchema[1]);
    }

    private void printDiagnosticMessages(
            SaveQueryDiagnostic saveQueryDiagnostic, long totalDuration, Integer level, Set<String> typeNames) {
        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            int messageLength = 50;
            String beginMessage = "Diagnostic Messages (Save)";

            Map<String, Object> diagnosticMessages = new LinkedHashMap<String, Object>();

            if (level != null) {
                diagnosticMessages.put("Level:", level);
            } else {
                beginMessage = "Diagnostic Summary Messages (Save)";
            }

            if (CollectionUtils.isNotEmpty(typeNames)) {
                int i = 1;
                for (String typeName : typeNames) {
                    if (typeName.length() > 40) {
                        typeName = StringUtils.abbreviateMiddle(typeName, "~", 40);
                    }

                    diagnosticMessages.put("Type" + i + ":", typeName);
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
            diagnosticMessages.put("Post Persistence Duration:",
                    saveQueryDiagnostic.getPostPersistDuration());
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
    public List<CreateQuery> create(List<CreateQuery> createQueries) {
        List<SaveQuery> saveQueries = new ArrayList<SaveQuery>();
        for (CreateQuery createQuery : createQueries) {
            saveQueries.add(createQuery);
        }

        save(saveQueries);

        return createQueries;
    }

    @Override
    public List<UpdateQuery> update(List<UpdateQuery> updateQueries) {
        List<SaveQuery> saveQueries = new ArrayList<SaveQuery>();
        for (UpdateQuery updateQuery : updateQueries) {
            saveQueries.add(updateQuery);
        }

        save(saveQueries);

        return updateQueries;
    }

    @Override
    public void delete(DeleteQuery deleteQuery) {
        if (!deleteQuery.isTruncated()) {
            PageReadQuery pageReadQuery = new PageReadQuery();
            pageReadQuery.setDomainType(deleteQuery.getDomainType());
            pageReadQuery.setConditions(deleteQuery.getConditions());
            pageReadQuery.setPage(1);
            pageReadQuery.setSize(10000);

            PageReadResults results = read(pageReadQuery);

            if (results != null) {
                if (results.getTotalPage() > 1) {
                    throw new IllegalStateException(
                            "More than 10000 rows will be deleted, the operation is interrupted for security reasons. "
                                    + "You can specify that the parameter `truncated` is true to continue");
                }
            }
        }

        SqlDeleteQuery sqlDeleteQuery = sqlQueryParser.getSqlDeleteQuery(deleteQuery);

        sqlQueryExecutor.execute(sqlDeleteQuery.getSql(), ((List) sqlDeleteQuery.getSqlParameters()).toArray());
    }

    @Override
    public void delete(List<DeleteQuery> deleteQueries) {
        for (DeleteQuery deleteQuery : deleteQueries) {
            delete(deleteQuery);
        }
    }

    @Override
    public Object execute(Queries queries) {
        Map<String, Object> results = new LinkedHashMap<>();

        for (Map.Entry<String, Object> queriesEntry : queries.entrySet()) {
            if (queriesEntry.getValue() instanceof List) {
                List resultsInGroup = new ArrayList();

                List<SaveQuery> saveQueries = new LinkedList<>();

                List<DeleteQuery> deleteQueries = new LinkedList<>();

                for (AbstractQuery query : (List<AbstractQuery>) queriesEntry.getValue()) {
                    switch (query.getQueryType()) {
                        case Create:
                        case Update:
                            saveQueries.add((SaveQuery) query);
                            break;
                        case Delete:
                            deleteQueries.add((DeleteQuery) query);
                            break;
                        default:
                            throw new UnsupportedOperationException(
                                    "Query type " + query.getQueryType() + " not supported");
                    }
                }

                resultsInGroup.addAll(save(saveQueries));

                delete(deleteQueries);

                results.put(queriesEntry.getKey(), resultsInGroup);
            } else {
                AbstractQuery query = (AbstractQuery) queriesEntry.getValue();

                switch (query.getQueryType()) {
                    case Read:
                        results.put(queriesEntry.getKey(), read((ReadQuery) query));
                        break;
                    case SingleRead:
                        results.put(queriesEntry.getKey(), read((SingleReadQuery) query));
                        break;
                    case TreeRead:
                        results.put(queriesEntry.getKey(), read((TreeReadQuery) query));
                        break;
                    case PageRead:
                        results.put(queriesEntry.getKey(), read((PageReadQuery) query));
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "Query type " + query.getQueryType() + " not supported");
                }
            }
        }

        return results;
    }
}
