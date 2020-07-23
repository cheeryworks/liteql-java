package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.delete.DeleteQuery;
import org.cheeryworks.liteql.query.read.PageReadQuery;
import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.query.read.SingleReadQuery;
import org.cheeryworks.liteql.query.read.TreeReadQuery;
import org.cheeryworks.liteql.query.read.result.PageReadResults;
import org.cheeryworks.liteql.query.read.result.ReadResult;
import org.cheeryworks.liteql.query.read.result.ReadResults;
import org.cheeryworks.liteql.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.query.save.CreateQuery;
import org.cheeryworks.liteql.query.save.UpdateQuery;
import org.cheeryworks.liteql.service.LiteQLService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface QueryService extends LiteQLService {

    QueryAccessDecisionService getQueryAccessDecisionService();

    ReadResults read(QueryContext queryContext, ReadQuery readQuery);

    ReadResult read(QueryContext queryContext, SingleReadQuery singleReadQuery);

    TreeReadResults read(QueryContext queryContext, TreeReadQuery treeReadQuery);

    PageReadResults read(QueryContext queryContext, PageReadQuery pageReadQuery);

    @Transactional
    CreateQuery create(QueryContext queryContext, CreateQuery createQuery);

    @Transactional
    UpdateQuery update(QueryContext queryContext, UpdateQuery updateQuery);

    @Transactional
    AbstractSaveQuery save(QueryContext queryContext, AbstractSaveQuery saveQuery);

    @Transactional
    List<AbstractSaveQuery> save(QueryContext queryContext, List<AbstractSaveQuery> saveQueries);

    @Transactional
    int delete(QueryContext queryContext, DeleteQuery deleteQuery);

    @Transactional
    void delete(QueryContext queryContext, List<DeleteQuery> deleteQueries);

    @Transactional
    Object execute(QueryContext queryContext, PublicQuery query);

}
