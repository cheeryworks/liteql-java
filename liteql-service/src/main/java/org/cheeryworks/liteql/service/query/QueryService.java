package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.query.CreateQuery;
import org.cheeryworks.liteql.model.query.DeleteQuery;
import org.cheeryworks.liteql.model.query.PageReadQuery;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.ReadQuery;
import org.cheeryworks.liteql.model.query.SingleReadQuery;
import org.cheeryworks.liteql.model.query.TreeReadQuery;
import org.cheeryworks.liteql.model.query.UpdateQuery;
import org.cheeryworks.liteql.model.query.result.ReadResult;
import org.cheeryworks.liteql.model.query.result.ReadResults;
import org.cheeryworks.liteql.model.query.result.PageReadResults;
import org.cheeryworks.liteql.model.query.result.TreeReadResults;

import java.util.List;

public interface QueryService {

    ReadResults read(ReadQuery readQuery);

    ReadResult read(SingleReadQuery singleReadQuery);

    TreeReadResults read(TreeReadQuery treeReadQuery);

    PageReadResults read(PageReadQuery pageReadQuery);

    List<CreateQuery> create(List<CreateQuery> createQueries);

    List<UpdateQuery> update(List<UpdateQuery> updateQueries);

    void delete(DeleteQuery deleteQuery);

    void delete(List<DeleteQuery> deleteQueries);

    Object execute(Queries queries);

}
