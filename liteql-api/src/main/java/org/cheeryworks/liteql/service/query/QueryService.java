package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
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
import org.cheeryworks.liteql.model.query.save.UpdateQuery;

import java.util.List;

public interface QueryService {

    ReadResults read(QueryContext queryContext, ReadQuery readQuery);

    ReadResult read(QueryContext queryContext, SingleReadQuery singleReadQuery);

    TreeReadResults read(QueryContext queryContext, TreeReadQuery treeReadQuery);

    PageReadResults read(QueryContext queryContext, PageReadQuery pageReadQuery);

    CreateQuery create(QueryContext queryContext, CreateQuery createQuery);

    UpdateQuery update(QueryContext queryContext, UpdateQuery updateQuery);

    AbstractSaveQuery save(QueryContext queryContext, AbstractSaveQuery saveQuery);

    List<AbstractSaveQuery> save(QueryContext queryContext, List<AbstractSaveQuery> saveQueries);

    int delete(QueryContext queryContext, DeleteQuery deleteQuery);

    void delete(QueryContext queryContext, List<DeleteQuery> deleteQueries);

    Object execute(QueryContext queryContext, PublicQuery query);

}
