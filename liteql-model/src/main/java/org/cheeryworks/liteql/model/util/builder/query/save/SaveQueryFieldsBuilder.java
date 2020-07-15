package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;

import java.util.Map;

public class SaveQueryFieldsBuilder<T extends AbstractSaveQuery> {

    private SaveQueryMetadata<T> saveQueryMetadata;

    public SaveQueryFieldsBuilder(SaveQueryMetadata<T> saveQueryMetadata) {
        this.saveQueryMetadata = saveQueryMetadata;
    }

    public SaveQueryAssociationsBuilder<T> fields(SaveFieldMetadata... saveFieldMetadataArray) {
        for (SaveFieldMetadata saveFieldMetadata : saveFieldMetadataArray) {
            this.saveQueryMetadata.getData().put(saveFieldMetadata.getName(), saveFieldMetadata.getValue());
        }

        return new SaveQueryAssociationsBuilder<>(this.saveQueryMetadata);
    }

    public SaveQueryAssociationsBuilder<T> fields(Map<String, Object> fields) {
        this.saveQueryMetadata.getData().putAll(fields);

        return new SaveQueryAssociationsBuilder<>(this.saveQueryMetadata);
    }

}
