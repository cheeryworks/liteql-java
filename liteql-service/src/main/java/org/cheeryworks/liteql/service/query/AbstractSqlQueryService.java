package org.cheeryworks.liteql.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
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
import org.cheeryworks.liteql.model.type.DomainTypeName;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.service.query.diagnostic.SaveQueryDiagnostic;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.util.SqlQueryServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public abstract class AbstractSqlQueryService implements QueryService {

    private static Logger logger = LoggerFactory.getLogger(AbstractSqlQueryService.class);

    private Repository repository;

    private ObjectMapper objectMapper;

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

    public AbstractSqlQueryService(
            Repository repository, ObjectMapper objectMapper,
            SqlQueryParser sqlQueryParser, SqlQueryExecutor sqlQueryExecutor) {
        this.repository = repository;
        this.objectMapper = objectMapper;
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

    private Object query(AbstractTypedReadQuery readQuery) {
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

    private ReadResults internalQuery(AbstractTypedReadQuery readQuery) {
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

    @Override
    public CreateQuery create(CreateQuery createQuery) {
        return (CreateQuery) save(Collections.singletonList(createQuery)).get(0);
    }

    @Override
    public UpdateQuery update(UpdateQuery updateQuery) {
        return (UpdateQuery) save(Collections.singletonList(updateQuery)).get(0);
    }

    @Override
    public AbstractSaveQuery save(AbstractSaveQuery saveQuery) {
        return save(Collections.singletonList(saveQuery)).get(0);
    }

    @Override
    public List<AbstractSaveQuery> save(List<AbstractSaveQuery> saveQueries) {
        long currentTime = System.currentTimeMillis();
        SaveQueryDiagnostic saveQueryDiagnostic = new SaveQueryDiagnostic();

        Map<Integer, Map<DomainTypeName, List<AbstractSaveQuery>>> sortedSaveQueries = new HashMap<>();

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
            Map<Integer, Map<DomainTypeName, List<AbstractSaveQuery>>> sortedSaveQueries,
            List<AbstractSaveQuery> saveQueries, int level) {
        long currentTime = System.currentTimeMillis();
        long duration = 0;

        Map<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithType = sortedSaveQueries.get(level);

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
            Map<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithType, int i) {
        long currentTime = System.currentTimeMillis();

        SaveQueryDiagnostic saveQueryDiagnostic = new SaveQueryDiagnostic();

        saveQueryDiagnostic.setAuditingEntitiesDuration(auditingEntities(saveQueriesWithType));

//        saveQueryDiagnostic.setBeforeSaveEventProcessingDuration(
//                publishBeforeSaveEvent(saveQueriesWithType, queryType));

        long[] persistResults = persist(saveQueriesWithType);

        saveQueryDiagnostic.setPrePersistDuration(persistResults[0]);
        saveQueryDiagnostic.setPersistDuration(persistResults[1]);
        saveQueryDiagnostic.setPersistCount(persistResults[2]);

//        saveQueryDiagnostic.setAfterSaveEventProcessingDuration(
//                publishAfterSaveEvent(saveQueriesWithType, queryType));

        saveQueryDiagnostic.setLinkingReferencesDuration(linkingParent(saveQueriesWithType));

        printDiagnosticMessages(
                saveQueryDiagnostic, System.currentTimeMillis() - currentTime, i, saveQueriesWithType.keySet());

        return saveQueryDiagnostic;
    }

    private long auditingEntities(Map<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
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

    private long linkingParent(Map<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithTypeEntry
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

    private long[] persist(Map<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithType) {
        long currentTime;
        long persistDuration = 0;
        long prePersistDuration = 0;
        long count = 0;

        currentTime = System.currentTimeMillis();

        prePersistDuration += System.currentTimeMillis() - currentTime;

        currentTime = System.currentTimeMillis();

        for (Map.Entry<DomainTypeName, List<AbstractSaveQuery>> saveQueriesWithTypeEntry
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
            Integer level, Set<DomainTypeName> domainTypeNames) {
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
                for (DomainTypeName domainTypeName : domainTypeNames) {
                    String domainTypeFullName = domainTypeName.getFullname();
                    if (domainTypeFullName.length() > 40) {
                        domainTypeFullName = StringUtils.abbreviateMiddle(domainTypeFullName, "~", 40);
                    }

                    diagnosticMessages.put("Type" + i + ":", domainTypeFullName);
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
    public int delete(DeleteQuery deleteQuery) {
        if (!deleteQuery.isTruncated()) {
            PageReadQuery pageReadQuery = new PageReadQuery();
            pageReadQuery.setDomainTypeName(deleteQuery.getDomainTypeName());
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

        return sqlQueryExecutor.execute(sqlDeleteQuery.getSql(), ((List) sqlDeleteQuery.getSqlParameters()).toArray());
    }

    @Override
    public void delete(List<DeleteQuery> deleteQueries) {
        for (DeleteQuery deleteQuery : deleteQueries) {
            delete(deleteQuery);
        }
    }

    @Override
    public Object execute(PublicQuery query) {
        if (query instanceof AbstractTypedReadQuery) {
            AbstractTypedReadQuery readQuery = (AbstractTypedReadQuery) query;

            return query(readQuery);
        } else if (query instanceof AbstractSaveQuery) {
            AbstractSaveQuery saveQuery = (AbstractSaveQuery) query;

            return save(saveQuery);
        } else if (query instanceof SaveQueries) {
            SaveQueries saveQueries = (SaveQueries) query;

            return save(saveQueries);
        } else if (query instanceof DeleteQuery) {
            DeleteQuery deleteQuery = (DeleteQuery) query;

            return delete(deleteQuery);
        } else if (query instanceof Queries) {
            Queries queries = (Queries) query;

            Map<String, Object> results = new LinkedHashMap<>();

            Iterator<String> fieldNamesIterator = queries.keySet().iterator();

            while (fieldNamesIterator.hasNext()) {
                String fieldName = fieldNamesIterator.next();

                results.put(fieldName, execute(queries.get(fieldName)));
            }

            return results;
        }

        throw new UnsupportedOperationException(LiteQLJsonUtil.toJson(this.objectMapper, query));
    }
}
