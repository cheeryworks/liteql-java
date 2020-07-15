package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.util.builder.query.QueryReference;

import java.util.List;

public class SaveQueryAssociationsBuilder<T extends AbstractSaveQuery> extends SaveQueryEndBuilder<T> {

    private SaveQueryMetadata<T> saveQueryMetadata;

    public SaveQueryAssociationsBuilder(SaveQueryMetadata<T> saveQueryMetadata) {
        super(saveQueryMetadata);

        this.saveQueryMetadata = saveQueryMetadata;
    }

    public SaveQueryEndBuilder<T> associations(
            List<QueryReference> references, SaveQueryMetadata... liteQLSaveQueries) {
        for (QueryReference reference : references) {
            saveQueryMetadata.getReferences().put(reference.getSource(), reference.getDestination());
        }

        saveQueryMetadata.setAssociations(liteQLSaveQueries);

        return new SaveQueryEndBuilder<>(saveQueryMetadata);
    }

}
