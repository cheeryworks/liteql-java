package org.cheeryworks.liteql.util.query.builder.save;

import org.cheeryworks.liteql.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.util.query.builder.QueryReference;

import java.util.List;

public class SaveQueryAssociationsBuilder<T extends AbstractSaveQuery> extends SaveQueryEndBuilder<T> {

    private SaveQueryMetadata<T> saveQueryMetadata;

    public SaveQueryAssociationsBuilder(SaveQueryMetadata<T> saveQueryMetadata) {
        super(saveQueryMetadata);

        this.saveQueryMetadata = saveQueryMetadata;
    }

    public SaveQueryEndBuilder<T> associations(
            List<QueryReference> references, SaveQueryMetadata... saveQueryMetadataArray) {
        for (QueryReference reference : references) {
            saveQueryMetadata.getReferences().put(reference.getSource(), reference.getDestination());
        }

        saveQueryMetadata.setAssociations(saveQueryMetadataArray);

        return new SaveQueryEndBuilder<>(saveQueryMetadata);
    }

}
