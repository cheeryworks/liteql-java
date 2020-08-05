package org.cheeryworks.liteql.schema.migration.operation;

import org.apache.commons.collections4.CollectionUtils;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.index.AbstractIndex;
import org.cheeryworks.liteql.schema.index.Index;
import org.cheeryworks.liteql.schema.index.Unique;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractIndexMigrationOperation<T extends AbstractIndex> extends AbstractMigrationOperation {

    private Set<T> indexes;

    public Set<T> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<T> indexes) {
        this.indexes = indexes;
    }

    public AbstractIndexMigrationOperation(MigrationOperationType type) {
        super(type);
    }

    @Override
    public void merge(DomainTypeDefinition domainTypeDefinition) {
        switch (getType()) {
            case CREATE_UNIQUE:
                for (T index : indexes) {
                    if (CollectionUtils.isEmpty(domainTypeDefinition.getUniques())) {
                        domainTypeDefinition.setUniques(new HashSet<>());
                    }

                    domainTypeDefinition.getUniques().add((Unique) index);
                }

                return;
            case CREATE_INDEX:
                for (T index : indexes) {
                    if (CollectionUtils.isEmpty(domainTypeDefinition.getIndexes())) {
                        domainTypeDefinition.setIndexes(new HashSet<>());
                    }

                    domainTypeDefinition.getIndexes().add((Index) index);
                }

                return;
            case DROP_UNIQUE:
                dropIndex(domainTypeDefinition.getUniques());

                return;
            case DROP_INDEX:
                dropIndex(domainTypeDefinition.getIndexes());

                return;
            default:
                return;
        }
    }

    private void dropIndex(Set<? extends AbstractIndex> existIndexes) {
        for (AbstractIndex index : indexes) {
            Iterator<? extends AbstractIndex> existIndexIterator = existIndexes.iterator();

            while (existIndexIterator.hasNext()) {
                if (existIndexIterator.next().equals(index)) {
                    existIndexIterator.remove();
                }
            }
        }
    }

}
