package org.cheeryworks.liteql.skeleton.service.query;

import org.cheeryworks.liteql.skeleton.model.DomainType;
import org.cheeryworks.liteql.skeleton.query.DefaultAuditQueryContext;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.SingleReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.TreeReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.page.Page;
import org.cheeryworks.liteql.skeleton.query.read.result.PageReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResult;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;

import java.util.Collection;
import java.util.List;

public interface QueryService {

    QueryContext EMPTY_QUERY_CONTEXT = new DefaultAuditQueryContext();

    default ReadResults read(ReadQuery readQuery) {
        return read(EMPTY_QUERY_CONTEXT, readQuery);
    }

    default <T extends DomainType> List<T> read(ReadQuery readQuery, Class<T> domainType) {
        return read(EMPTY_QUERY_CONTEXT, readQuery, domainType);
    }

    <T extends DomainType> List<T> read(QueryContext queryContext, ReadQuery readQuery, Class<T> domainType);

    ReadResults read(QueryContext queryContext, ReadQuery readQuery);

    default ReadResult read(SingleReadQuery singleReadQuery) {
        return read(EMPTY_QUERY_CONTEXT, singleReadQuery);
    }

    default <T extends DomainType> T read(SingleReadQuery singleReadQuery, Class<T> domainType) {
        return read(EMPTY_QUERY_CONTEXT, singleReadQuery, domainType);
    }

    <T extends DomainType> T read(QueryContext queryContext, SingleReadQuery singleReadQuery, Class<T> domainType);

    ReadResult read(QueryContext queryContext, SingleReadQuery singleReadQuery);

    default TreeReadResults read(TreeReadQuery treeReadQuery) {
        return read(EMPTY_QUERY_CONTEXT, treeReadQuery);
    }

    TreeReadResults read(QueryContext queryContext, TreeReadQuery treeReadQuery);

    default PageReadResults read(PageReadQuery pageReadQuery) {
        return read(EMPTY_QUERY_CONTEXT, pageReadQuery);
    }

    default <T extends DomainType> Page<T> read(PageReadQuery pageReadQuery, Class<T> domainType) {
        return read(EMPTY_QUERY_CONTEXT, pageReadQuery, domainType);
    }

    <T extends DomainType> Page<T> read(QueryContext queryContext, PageReadQuery pageReadQuery, Class<T> domainType);

    PageReadResults read(QueryContext queryContext, PageReadQuery pageReadQuery);

    default CreateQuery create(CreateQuery createQuery) {
        return create(EMPTY_QUERY_CONTEXT, createQuery);
    }

    default <T extends DomainType> T create(T domainEntity) {
        return create(EMPTY_QUERY_CONTEXT, domainEntity);
    }

    <T extends DomainType> T create(QueryContext queryContext, T domainEntity);

    default <T extends DomainType> List<T> create(Collection<T> domainEntities) {
        return create(EMPTY_QUERY_CONTEXT, domainEntities);
    }

    <T extends DomainType> List<T> create(QueryContext queryContext, Collection<T> domainEntities);

    CreateQuery create(QueryContext queryContext, CreateQuery createQuery);

    default UpdateQuery update(UpdateQuery updateQuery) {
        return update(EMPTY_QUERY_CONTEXT, updateQuery);
    }

    default <T extends DomainType> T update(T domainEntity) {
        return update(EMPTY_QUERY_CONTEXT, domainEntity);
    }

    <T extends DomainType> T update(QueryContext queryContext, T domainEntity);

    default <T extends DomainType> List<T> update(Collection<T> domainEntities) {
        return update(EMPTY_QUERY_CONTEXT, domainEntities);
    }

    <T extends DomainType> List<T> update(QueryContext queryContext, Collection<T> domainEntities);

    UpdateQuery update(QueryContext queryContext, UpdateQuery updateQuery);

    default AbstractSaveQuery save(AbstractSaveQuery saveQuery) {
        return save(EMPTY_QUERY_CONTEXT, saveQuery);
    }

    AbstractSaveQuery save(QueryContext queryContext, AbstractSaveQuery saveQuery);

    default List<AbstractSaveQuery> save(List<AbstractSaveQuery> saveQueries) {
        return save(EMPTY_QUERY_CONTEXT, saveQueries);
    }

    List<AbstractSaveQuery> save(QueryContext queryContext, List<AbstractSaveQuery> saveQueries);

    default int delete(DeleteQuery deleteQuery) {
        return delete(EMPTY_QUERY_CONTEXT, deleteQuery);
    }

    int delete(QueryContext queryContext, DeleteQuery deleteQuery);

    default void delete(List<DeleteQuery> deleteQueries) {
        delete(EMPTY_QUERY_CONTEXT, deleteQueries);
    }

    void delete(QueryContext queryContext, List<DeleteQuery> deleteQueries);

    default Object execute(PublicQuery query) {
        return execute(EMPTY_QUERY_CONTEXT, query);
    }

    Object execute(QueryContext queryContext, PublicQuery query);

}
