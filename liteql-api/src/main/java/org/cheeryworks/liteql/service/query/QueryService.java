package org.cheeryworks.liteql.service.query;

import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.delete.DeleteQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.query.read.SingleReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.model.query.read.result.PageReadResults;
import org.cheeryworks.liteql.model.query.read.result.ReadResult;
import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;

import java.util.List;

public interface QueryService {

    ReadResults read(ReadQuery readQuery);

    ReadResult read(SingleReadQuery singleReadQuery);

    TreeReadResults read(TreeReadQuery treeReadQuery);

    PageReadResults read(PageReadQuery pageReadQuery);

    CreateQuery create(CreateQuery createQuery);

    UpdateQuery update(UpdateQuery updateQuery);

    AbstractSaveQuery save(AbstractSaveQuery saveQuery);

    List<AbstractSaveQuery> save(List<AbstractSaveQuery> saveQueries);

    int delete(DeleteQuery deleteQuery);

    void delete(List<DeleteQuery> deleteQueries);

    Object execute(PublicQuery query);

}
